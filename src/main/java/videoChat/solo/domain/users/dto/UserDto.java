package videoChat.solo.domain.users.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private String username;

    private String email;

    private String password;

    private String confirmPassword;

}
