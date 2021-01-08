package hr.documentcloud.web;

import hr.documentcloud.model.DocumentDto;
import hr.documentcloud.service.DocumentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

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

    @PostMapping("create-directory")
    public void createDirectory(@RequestParam("directory") String newDirectory) {
        log.info("Received request to create a new directory: '{}'.", newDirectory);
        try {
            documentService.createDirectory(newDirectory);
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

    @GetMapping(value = "get-file")
    public void getFile(@RequestParam("file") String fileAbsolutePath, HttpServletResponse response) {
        log.info("Received request to fetch file '{}'.", fileAbsolutePath);
        try {
            String fileName = documentService.determineFileName(fileAbsolutePath);
            response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            documentService.writeFileToStream(fileAbsolutePath, response.getOutputStream());
        } catch(Exception e) {
            log.error("Error occurred: ", e);
//            throw e; // todo
        }
    }

    @GetMapping(value="get-files", produces="application/zip")
    public void getFiles(@RequestParam("files") List<String> filePaths, HttpServletResponse response) {
        log.info("Received request to create a .zip with files {}.", filePaths);

        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"files.zip\"");

        try {
            documentService.writeFilesZipToStream(filePaths, response.getOutputStream());
        } catch(Exception e) {
            log.error("Error occurred: ", e);
            // TODO: handle
        }
    }

    @GetMapping(value="get-directory", produces="application/zip")
    public void getDirectory(@RequestParam("directory") String directory, HttpServletResponse response) {
        log.info("Received request to create a .zip of directory {}.", directory);

        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"" + directory + ".zip\"");

        try {
            documentService.writeDirectoryZipToStream(directory, response.getOutputStream());
        } catch(Exception e) {
            log.error("Error occurred: ", e);
            // TODO: handle
        }
    }

}
