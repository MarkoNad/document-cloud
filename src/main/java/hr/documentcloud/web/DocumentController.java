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

    @PostMapping("upload-file")
    public void receiveFile(@RequestParam("file") MultipartFile file, @RequestParam("absolute-path") String absolutePath) {
        log.info("Received request to store file '{}' to {}.", file, absolutePath);
        try {
            documentService.storeFile(file, absolutePath);
        } catch(Exception e) {
            log.error("Error occurred: ", e);
            throw e;
        }
    }

    @GetMapping("get-files-details")
    public @ResponseBody List<DocumentDto> getFilesDetails(@RequestParam("directory") String directory) {
        log.info("Received request to fetch details for files in directory '{}'.", directory);
        try {
            return documentService.fetchFilesDetails(directory);
        } catch(Exception e) {
            log.error("Error occurred: ", e);
            throw e;
        }
    }

}
