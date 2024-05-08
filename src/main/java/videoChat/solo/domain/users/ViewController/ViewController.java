package videoChat.solo.domain.users.ViewController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping(value = {"/register"})
    public String register() {
        return "member/register";
    }

    @GetMapping(value = {"/login"})
    public String login() {
        return "member/login";
    }
}
