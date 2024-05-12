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

    public UserDto(String email) {
        this.email = email;
    }

    public UserDto(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
