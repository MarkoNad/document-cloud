package hr.documentcloud.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController("/")
@Log4j2
public class DocumentController {

    @GetMapping("getDocument/{documentId}")
    public @ResponseBody String getDocument(@PathVariable("documentId") String documentId) {
        log.info("Fetching document.");
        return "Greetings from Spring Boot!";
    }

    @PostMapping("upload-file")
    public String receiveFile(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) throws IOException {

        log.info("File: {}", file);
        log.info("File: {}", file.getOriginalFilename());

        String contents = new String(file.getBytes(), StandardCharsets.UTF_8);
        log.info("Contents: " + contents);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @PostMapping("upload-files")
    public String receiveFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        log.info("Receiving files: {}", files);
        for (MultipartFile file : files) {
            log.info("File: {}", file);
            log.info("File: {}", file.getOriginalFilename());
            String contents = new String(file.getBytes(), StandardCharsets.UTF_8);
            log.info("Contents: " + contents);
        }

        return "redirect:/";
    }

}
