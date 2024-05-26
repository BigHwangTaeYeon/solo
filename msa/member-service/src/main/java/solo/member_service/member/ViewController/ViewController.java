package solo.member_service.member.ViewController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
//@RestController
//@RequestMapping("/member-service") // -> 게이트웨이 yml파일에 적은 path를 써줘야함
public class ViewController {
    @GetMapping(value = {"/", "index", "main"})
    public String index() {
        return "index";
    }

    @GetMapping(value = {"/register"})
    public String register() {
        return "member/register";
    }

    @GetMapping(value = {"/login"})
    public String login() {
        return "member/login";
    }
}
