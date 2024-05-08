package videoChat.solo.domain.users.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private String username;

    private String email;

    private String password;

    private String confirm_password;

}
