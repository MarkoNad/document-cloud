package hr.documentcloud.web;

import hr.documentcloud.model.DocumentDto;
import hr.documentcloud.service.DocumentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
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
    public ResponseEntity<InputStream> getFile(@RequestParam("file") String file) {
        log.info("Received request to fetch file '{}'.", file);
        try {
            InputStream inputStreamResource = documentService.fetchFileStream(file);
            return new ResponseEntity<>(inputStreamResource, HttpStatus.OK);
        } catch(Exception e) {
            log.error("Error occurred: ", e);
            return null; // todo
//            throw e;
        }
    }

    @GetMapping(value="get-files", produces="application/zip")
    public void getFiles(@RequestParam("files") List<String> filePaths, HttpServletResponse response) {
        log.info("Received request to create a .zip with files {}.", filePaths);

        response.setStatus(HttpServletResponse.SC_OK);
        response.addHeader("Content-Disposition", "attachment; filename=\"test.zip\"");

        try {
            documentService.writeZipToStream(filePaths, response.getOutputStream());
        } catch(Exception e) {
            log.error("Error occurred: ", e);
            // TODO: handle
        }
    }

//    @GetMapping(value="get-files", produces="application/zip")
//    public ResponseEntity<InputStreamResource> getFiles(HttpServletResponse response) throws Exception {
//
//        //setting headers
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.addHeader("Content-Disposition", "attachment; filename=\"test.zip\"");
//
//        ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
//
//        // create a list to add files to be zipped
//        ArrayList<File> files = new ArrayList<>(2);
//        files.add(new File("README.md"));
//
//        // package files
//        for (File file : files) {
//            //new zip entry and copying inputstream with file to zipOutputStream, after all closing streams
//            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
//            FileInputStream fileInputStream = new FileInputStream(file);
//
//            IOUtils.copy(fileInputStream, zipOutputStream);
//
//            fileInputStream.close();
//            zipOutputStream.closeEntry();
//        }
//
//        zipOutputStream.close();
//
//
//
//
//        HttpHeaders httpHeaders = createHttpHeaders();
//
//
//        InputStreamResource inputStreamResource = new InputStreamResource(zipOutputStream);
//        httpHeaders.setContentLength(contentLengthOfStream);
//        return new ResponseEntity<>(inputStreamResource, httpHeaders, HttpStatus.OK);
//    }

}
