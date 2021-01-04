package hr.documentcloud.service;

import hr.documentcloud.dal.DirectoryStructureContainer;
import hr.documentcloud.dal.DirectoryStructureRepository;
import hr.documentcloud.dal.File;
import hr.documentcloud.dal.FileRepository;
import hr.documentcloud.exception.FIleStoringException;
import hr.documentcloud.model.DirectoryStructure;
import hr.documentcloud.model.DocumentDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void storeFile(MultipartFile file, String destination) {
        try {
            storeFilePrivate(file, destination);
        } catch (Exception e) {
            throw new FIleStoringException(e);
        }
    }

    private void storeFilePrivate(MultipartFile file, String destination) throws IOException {
        log.info("File: {}", file);
        log.info("File: {}", file.getOriginalFilename());
        log.info("File: {}", file.getName());
        log.info("Destination: {}", destination);

        DirectoryStructure directoryStructure = getDirectoryStructure();


        String contents = new String(file.getBytes(), StandardCharsets.UTF_8);
        log.info("Contents: " + contents);


        File FIle = new File(file.getOriginalFilename(), destination, file.getBytes());
        log.info("Persisting document {}", FIle);
        fileRepository.save(FIle);
        log.info("Persisted.");
    }

    private DirectoryStructure getDirectoryStructure(String destination) {
        Optional<DirectoryStructureContainer> maybeContainer = directoryStructureRepository.getById(1L);

        if (maybeContainer.isPresent()) {
            DirectoryStructureContainer container = maybeContainer.get();
            DirectoryStructure structure =
            update;
            return;
        }


    }

    private void updateDirectoryStructure(String directory) {
        DirectoryStructure structure = null; // TODO
        structure.updateStructure(directory);

    }

    public List<DocumentDto> fetchFilesDetails(String directory) {
        log.info("Fetching files from directory '{}'.", directory);
        List<File> files = fileRepository.fetchFilesFromDirectory(directory);
        List<DocumentDto> dtos = files.stream()
                .map(d -> new DocumentDto(d.getName()))
                .collect(Collectors.toList());
        log.info("Got file DTOs: {}", dtos);
        return dtos;
    }

}
