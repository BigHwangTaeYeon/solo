package videoChat.solo.domain.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping(value = {"", "/", "/home", "/index", "main"})
    public String index() {
        return "index";
    }
}
