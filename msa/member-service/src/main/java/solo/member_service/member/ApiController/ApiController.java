package solo.member_service.member.ApiController;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import solo.member_service.member.dto.UserDto;
import solo.member_service.member.service.UserDetailsServiceImpl;

@RestController
//@RequestMapping("/member-service") // -> 게이트웨이 yml파일에 적은 path를 써줘야함
public class ApiController {

    private final UserDetailsServiceImpl userDetailsService;

    public ApiController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(value = "/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserDto userDto){
        userDetailsService.save(userDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping(value = "/signUpTest")
    public ResponseEntity<?> signUpTest(@RequestBody UserDto userDto){
        userDetailsService.save(userDto);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping(value = "/getEmail")
    public ResponseEntity<?> getEmail(HttpServletRequest request){
        return ResponseEntity.ok(userDetailsService.emailByToken(request.getHeader(HttpHeaders.AUTHORIZATION)));
    }
}
