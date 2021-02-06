package hr.documentcloud.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
@Log4j2
public class LoginController {

//    @PostMapping("login")
    @GetMapping("login")
//    @RequestMapping("login")
    public void login() {
        log.info("Login called");
    }

    @GetMapping("home")
//    @PostMapping("home")
    public void home() {
        log.info("home called");
    }

}
