package videoChat.solo.domain.users.ApiController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import videoChat.solo.domain.users.dto.UserDto;
import videoChat.solo.domain.users.entity.Users;
import videoChat.solo.domain.users.jwt.JwtServiceImpl;
import videoChat.solo.domain.users.service.UserDetailsServiceImpl;

@Controller
public class ApiController {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtServiceImpl jwtService;

    public ApiController(UserDetailsServiceImpl userDetailsService, JwtServiceImpl jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserDto userDto){
        userDetailsService.save(userDto);
        return null;
    }

    @PostMapping(value = "/test")
    public ResponseEntity<?> test(@RequestBody UserDto userDto, HttpServletRequest request){

        jwtService.isTokenValid(jwtService.extractAccessToken(request).orElseThrow());

        return null;
    }

}
