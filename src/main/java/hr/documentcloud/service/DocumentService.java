package hr.documentcloud.service;

import hr.documentcloud.dal.DirectoryStructureContainer;
import hr.documentcloud.dal.DirectoryStructureRepository;
import hr.documentcloud.dal.File;
import hr.documentcloud.dal.FileRepository;
import hr.documentcloud.dal.util.LobHelper;
import hr.documentcloud.exception.FileStoringException;
import hr.documentcloud.exception.ResourceNotFoundException;
import hr.documentcloud.exception.ZipGenerationException;
import hr.documentcloud.model.DirectoryStructure;
import hr.documentcloud.model.DocumentDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static hr.documentcloud.model.DirectoryStructure.DEFAULT_DIRECTORY_DELIMITER;
import static hr.documentcloud.model.Type.DIRECTORY;
import static hr.documentcloud.model.Type.FILE;

@Service
@Log4j2
public class DocumentService {

    private final FileRepository fileRepository;
    private final DirectoryStructureRepository directoryStructureRepository;
    private final LobHelper lobHelper;
    private static final String ZIP_DIRECTORY_DELIMITER = "/";

    @Autowired
    public DocumentService(
            FileRepository fileRepository,
            DirectoryStructureRepository directoryStructureRepository,
            LobHelper lobHelper) {
        this.fileRepository = fileRepository;
        this.directoryStructureRepository = directoryStructureRepository;
        this.lobHelper = lobHelper;
    }

    public void storeFile(MultipartFile file, String absolutePath) {
        try {
            storeFilePrivate(file, absolutePath);
        } catch (Exception e) {
            throw new FileStoringException(e);
        }
    }

    private void storeFilePrivate(MultipartFile multipartFile, String absolutePath) throws IOException {
        String destinationDirectory = determineDestinationDirectory(absolutePath);
        updateDirectoryStructure(destinationDirectory);

        String fileName = determineFileName(absolutePath);
        persistFile(multipartFile, fileName, destinationDirectory);
    }

    private void updateDirectoryStructure(String destinationDirectory) {
        log.info("Updating directory structure with destination directory '{}'.", destinationDirectory);

        Optional<DirectoryStructureContainer> maybeContainer = directoryStructureRepository.get();

        if (maybeContainer.isPresent()) {
            DirectoryStructureContainer container = maybeContainer.get();
            DirectoryStructure structure = container.getDirectoryStructure();
            structure.updateStructure(destinationDirectory);
            container.update(structure);
            log.info("Updating existing directory structure.");
            directoryStructureRepository.merge(container);
        } else {
            DirectoryStructure structure = DirectoryStructure.fromPath(destinationDirectory);
            DirectoryStructureContainer container = new DirectoryStructureContainer(structure);
            log.info("Inserting new directory structure.");
            directoryStructureRepository.persist(container);
        }
    }

    private String determineDestinationDirectory(String destination) {
        String directory = destination.substring(0, destination.lastIndexOf(DEFAULT_DIRECTORY_DELIMITER));
        log.info("Calculated directory: '{}'.", directory);
        return directory;
    }

    public String determineFileName(String absolutePath) {
        log.info("Calculating file name from absolute path: '{}'.", absolutePath);
        String fileName = absolutePath.substring(1 + absolutePath.lastIndexOf(DEFAULT_DIRECTORY_DELIMITER));
        log.info("Calculated file name: '{}'.", fileName);
        return fileName;
    }

    private void persistFile(MultipartFile multipartFile, String fileName, String destinationDirectory) throws IOException {
        Blob newContents = lobHelper.createBlob(multipartFile.getInputStream(), multipartFile.getSize());

        Optional<File> maybeFile = fileRepository.getByNameAndLocation(fileName, destinationDirectory);

        if (maybeFile.isPresent()) {
            log.info("File '{}' already exists in folder '{}'; updating its contents.", fileName, destinationDirectory);
            File existingFile = maybeFile.get();
            existingFile.setContents(newContents);
            fileRepository.merge(existingFile);
            return;
        }

        File file = new File(fileName, destinationDirectory, newContents);
        fileRepository.persist(file);
    }

    public List<DocumentDto> fetchFilesDetails(String directory) {
        log.info("Fetching files from directory '{}'.", directory);
        List<File> files = fileRepository.fetchFilesFromDirectory(directory);
        List<DocumentDto> dtos = files.stream()
                .map(d -> new DocumentDto(d.getName(), FILE))
                .collect(Collectors.toList());
        log.info("Got file DTOs: {}", dtos);

        Optional<DirectoryStructureContainer> maybeContainer = directoryStructureRepository.get();

        if (!maybeContainer.isPresent()) {
            log.info("Directory structure not found; returning only file details.");
            return dtos;
        }

        DirectoryStructure structure = maybeContainer.get().getDirectoryStructure();
        structure.getSubfolderNames(directory)
                .forEach(subfolder -> dtos.add(new DocumentDto(subfolder, DIRECTORY)));

        return dtos;
    }

    public void createDirectory(String newDirectory) {
        updateDirectoryStructure(newDirectory);
    }

    public void writeFileToStream(String fileAbsolutePath, OutputStream outputStream) throws SQLException, IOException {
        String location = determineDestinationDirectory(fileAbsolutePath);
        String fileName = determineFileName(fileAbsolutePath);

        File file = fileRepository.getByNameAndLocation(fileName, location)
                .orElseThrow(() -> new ResourceNotFoundException("File " + fileName + " not found in " + location + "."));

        Blob blob = file.getContents();
        lobHelper.writeBlobToOutputStream(blob, outputStream);
    }

    public void writeFilesZipToStream(List<String> filePaths, OutputStream outputStream) {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (String path : filePaths) {
                log.info("Adding file '{}' to .zip.", path);
                String fileName = determineFileName(path);
                String location = determineDestinationDirectory(path);

                Optional<File> maybeFile = fileRepository.getByNameAndLocation(fileName, location);
                if (!maybeFile.isPresent()) {
                    log.warn("File '{}' not found; will not add it to archive.", path);
                    continue;
                }

                File file = maybeFile.get();

                addToZip(zipOutputStream, fileName, file);
            }
        } catch (IOException | SQLException e) {
            throw new ZipGenerationException("Failed to generate .zip.", e);
        } catch (Exception e) {
            throw new ZipGenerationException(e);
        }

        log.info("Added all files.");
    }

    private void addToZip(ZipOutputStream zipOutputStream, String fileName, File file) throws IOException, SQLException {
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        lobHelper.writeBlobToOutputStream(file.getContents(), zipOutputStream);
        zipOutputStream.closeEntry();
    }

    public void writeDirectoryZipToStream(String directory, ServletOutputStream outputStream) {
        DirectoryStructure directoryStructure = directoryStructureRepository.get()
                .orElseThrow(() -> new ResourceNotFoundException("Directory structure is not defined; can't find directory '" + directory + "'."))
                .getDirectoryStructure();

        DirectoryStructure requiredDir = directoryStructure.find(directory)
                .orElseThrow(() -> new ResourceNotFoundException("Directory '" + directory + "' not found."));

        List<File> files = fileRepository.fetchFilesFromDirectoryRecursively(directory);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (File file : files) {
                String absolutePath = file.getPath() + DEFAULT_DIRECTORY_DELIMITER + file.getName();
                log.info("Adding file '{}' to .zip.", absolutePath);
                addToZip(zipOutputStream, absolutePath, file);
            }
        } catch (IOException | SQLException e) {
            throw new ZipGenerationException("Failed to generate .zip.", e);
        } catch (Exception e) {
            throw new ZipGenerationException(e);
        }

        log.info("Added all files.");
    }

//    public void writeDirectoryZipToStream(String directory, ServletOutputStream outputStream) {
//        DirectoryStructure directoryStructure = directoryStructureRepository.get()
//                .orElseThrow(() -> new ResourceNotFoundException("Directory structure is not defined; can't find directory '" + directory + "'."))
//                .getDirectoryStructure();
//
//        DirectoryStructure requiredDir = directoryStructure.find(directory)
//                .orElseThrow(() -> new ResourceNotFoundException("Directory '" + directory + "' not found."));
//
//        try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
//            ZipEntry zippedDir = new ZipEntry(requiredDir.getDirectoryName() + ZIP_DIRECTORY_DELIMITER);
//            zipOutputStream.putNextEntry(zippedDir);
//
//            List<File> filesInDir = fileRepository.fetchFilesFromDirectory(dirAbsolutePath);
//
//            for (File file : filesInDir) {
//                zippedDir.
//            }
//
//            for (String path : filePaths) {
//                log.info("Adding file '{}' to .zip.", path);
//                String fileName = determineFileName(path);
//                String location = determineDestinationDirectory(path);
//
//                Optional<File> maybeFile = fileRepository.getByNameAndLocation(fileName, location);
//                if (!maybeFile.isPresent()) {
//                    log.warn("File '{}' not found; will not add it to archive.", path);
//                    continue;
//                }
//
//                File file = maybeFile.get();
//
//                addToZip(zipOutputStream, fileName, file);
//            }
//        } catch (IOException | SQLException e) {
//            throw new ZipGenerationException("Failed to generate .zip.", e);
//        } catch (Exception e) {
//            throw new ZipGenerationException(e);
//        }
//
//        log.info("Added all files.");
//    }
}
