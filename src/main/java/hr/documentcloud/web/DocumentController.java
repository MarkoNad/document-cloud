package hr.documentcloud.web;

import hr.documentcloud.model.DocumentDto;
import hr.documentcloud.service.DocumentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController("/")
@Log4j2
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("get-file/{fileId}")
    public @ResponseBody String getFile(@PathVariable("fileId") String fileId) {
        log.info("Fetching document.");
        return "Greetings from Spring Boot!";
    }

    @PostMapping("upload-file")
    public void receiveFile(@RequestParam("file") MultipartFile file, @RequestParam("destination") String destination) {
        log.info("Received request to store file '{}' to {}.", file, destination);
        try {
            documentService.storeFile(file, destination);
        } catch(Exception e) {
            log.error("Error occurred: ", e);
            throw e;
        }
    }

    @GetMapping("get-files-details/{directory}")
    public @ResponseBody List<DocumentDto> getFilesDetails(@PathVariable("directory") String directory) {
        log.info("Received request to fetch details for files in directory '{}'.", directory);
        try {
            return documentService.fetchFilesDetails(directory);
        } catch(Exception e) {
            log.error("Error occurred: ", e);
            throw e;
        }
    }

}
