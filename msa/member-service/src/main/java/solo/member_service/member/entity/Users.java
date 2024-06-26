package solo.member_service.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import solo.member_service.member.dto.UserDto;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false)
    private String password;

    private LocalDate createdAt;

    //== jwt.yml 토큰 추가 ==//
    @Column(length = 1000)
    private String refreshToken;

    public Users(UserDto dto) {
        this.name = dto.getUsername();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        encodePassword(new BCryptPasswordEncoder());
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken() {
        this.refreshToken = null;
    }

    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = "{bcrypt}" + passwordEncoder.encode(password);
    }
}
