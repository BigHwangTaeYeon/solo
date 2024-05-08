package videoChat.solo.domain.users.ApiController;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import videoChat.solo.domain.users.dto.UserDto;
import videoChat.solo.domain.users.service.UserDetailsServiceImpl;

@Controller
public class ApiController {

    private final UserDetailsServiceImpl userDetailsService;

    public ApiController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(value = "/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserDto userDto){
        System.out.println("userDto.getName() = " + userDto.getUsername());
        System.out.println("test");

        userDetailsService.save(userDto);

        return null;
    }

}
