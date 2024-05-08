package videoChat.solo.domain.users.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import videoChat.solo.domain.users.jwt.JwtService;
import videoChat.solo.domain.users.repository.UsersRepository;

import java.io.IOException;

@Slf4j
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UsersRepository usersRepository;

    public LoginSuccessJWTProvideHandler(JwtService jwtService, UsersRepository usersRepository) {
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = extractEmail(authentication);
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        usersRepository.findByEmail(email).ifPresent(
                users -> users.updateRefreshToken(refreshToken)
        );

        log.info( "로그인에 성공합니다. email: {}" , email);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}" ,accessToken);
        log.info( "RefreshToken 을 발급합니다. RefreshToken: {}" ,refreshToken);

        response.getWriter().write("success");
    }

    private String extractEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
