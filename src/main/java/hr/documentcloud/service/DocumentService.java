package hr.documentcloud.service;

import hr.documentcloud.dal.DirectoryStructureContainer;
import hr.documentcloud.dal.DirectoryStructureRepository;
import hr.documentcloud.dal.File;
import hr.documentcloud.dal.FileRepository;
import hr.documentcloud.exception.FileStoringException;
import hr.documentcloud.exception.ResourceNotFoundException;
import hr.documentcloud.model.DirectoryStructure;
import hr.documentcloud.model.DocumentDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static hr.documentcloud.model.DirectoryStructure.DEFAULT_DIRECTORY_DELIMITER;
import static hr.documentcloud.model.Type.DIRECTORY;
import static hr.documentcloud.model.Type.FILE;

@Service
@Log4j2
public class DocumentService {

    private final FileRepository fileRepository;
    private final DirectoryStructureRepository directoryStructureRepository;

    @Autowired
    public DocumentService(FileRepository fileRepository, DirectoryStructureRepository directoryStructureRepository) {
        this.fileRepository = fileRepository;
        this.directoryStructureRepository = directoryStructureRepository;
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

    private String determineFileName(String absolutePath) {
        log.info("Calculating file name from absolute path: '{}'.", absolutePath);
        String fileName = absolutePath.substring(1 + absolutePath.lastIndexOf(DEFAULT_DIRECTORY_DELIMITER));
        log.info("Calculated file name: '{}'.", fileName);
        return fileName;
    }

    private void persistFile(MultipartFile multipartFile, String fileName, String destinationDirectory) throws IOException {
        final byte[] newContents = multipartFile.getBytes();

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

    public ResponseEntity<byte[]> fetchFile(String fileAbsolutePath) {
        String location = determineDestinationDirectory(fileAbsolutePath);
        String fileName = determineFileName(fileAbsolutePath);

        File file = fileRepository.getByNameAndLocation(fileName, location)
                .orElseThrow(() -> new ResourceNotFoundException("File " + fileName + " not found in " + location + "."));

        return ResponseEntity
                .ok()
                .body(file.getContents());
    }

}
