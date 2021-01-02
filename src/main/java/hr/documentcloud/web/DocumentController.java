package hr.documentcloud.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController("/")
@Log4j2
public class DocumentController {

    @GetMapping("getDocument/{documentId}")
    public @ResponseBody String getDocument(@PathVariable("documentId") String documentId) {
        log.info("Fetching document.");
        return "Greetings from Spring Boot!";
    }

//    @PostMapping("addDocument")
//    public @ResponseBody String addDocument() {
//        log.info("Adding document.");
//        return "Document added.";
//    }

    @PostMapping("upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {

        String contents = new String(file.getBytes(), StandardCharsets.UTF_8);
        log.info("Contents: " + contents);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

}
