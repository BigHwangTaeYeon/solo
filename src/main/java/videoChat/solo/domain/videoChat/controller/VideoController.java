package videoChat.solo.domain.videoChat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VideoController {

    @RequestMapping("/test")
    public String test() {
        return "test";
    }
    @RequestMapping("/home")
    public String home() {
        return "home";
//        return "content/home";
    }
    @RequestMapping("/index")
    public String index() {
        return "video/index";
//        return "content/video/index";
    }
}
