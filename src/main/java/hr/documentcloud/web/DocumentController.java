package hr.documentcloud.web;

import hr.documentcloud.model.DocumentDto;
import hr.documentcloud.service.DocumentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public void receiveFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("absolute-path") String absolutePath,
            @RequestParam("last-modified") Long lastModifiedMilliseconds) {
        log.info("Received request to store file '{}' with last modified date {} to {}.", file, lastModifiedMilliseconds, absolutePath);
        documentService.storeFile(file, absolutePath, lastModifiedMilliseconds);
    }

    @PostMapping("create-directory")
    public void createDirectory(@RequestParam("directory") String newDirectory) {
        log.info("Received request to create a new directory: '{}'.", newDirectory);
        documentService.createDirectory(newDirectory);
    }

    @GetMapping("get-files-details")
    public @ResponseBody List<DocumentDto> getFilesDetails(@RequestParam("directory") String directory) {
        log.info("Received request to fetch details for files in directory '{}'.", directory);
        return documentService.fetchFilesDetails(directory);
    }

    @GetMapping(value = "get-file")
    public void getFile(@RequestParam("file") String fileAbsolutePath, HttpServletResponse response) throws IOException {
        log.info("Received request to fetch file '{}'.", fileAbsolutePath);
        String fileName = documentService.determineFileName(fileAbsolutePath);
        response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        documentService.writeFileToStream(fileAbsolutePath, response.getOutputStream());
    }

    @GetMapping(value="get-files", produces="application/zip")
    public void getFiles(@RequestParam("files") List<String> filePaths, HttpServletResponse response) throws IOException {
        log.info("Received request to create a .zip with files {}.", filePaths);
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"files.zip\"");
        documentService.writeFilesZipToStream(filePaths, response.getOutputStream());
    }

    @GetMapping(value="get-directory", produces="application/zip")
    public void getDirectory(@RequestParam("directory") String directoryAbsolutePath, HttpServletResponse response) throws IOException {
        log.info("Received request to create a .zip of directory {}.", directoryAbsolutePath);
        String directoryName = documentService.determineDirectoryName(directoryAbsolutePath);
        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"" + directoryName + ".zip\"");
        documentService.writeDirectoryZipToStream(directoryAbsolutePath, response.getOutputStream());
    }

}
