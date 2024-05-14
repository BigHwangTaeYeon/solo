package videoChat.solo.com.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import videoChat.solo.domain.users.entity.RefreshTokenEntity;
import videoChat.solo.domain.users.entity.UserDetailsImpl;
import videoChat.solo.domain.users.jwt.JwtService;
import videoChat.solo.domain.users.repository.RefreshTokenRepository;
import videoChat.solo.domain.users.repository.UsersRepository;

import java.io.IOException;

@Slf4j
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginSuccessJWTProvideHandler(JwtService jwtService, UsersRepository usersRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = extractEmail(authentication);
        String accessToken = jwtService.createAccessToken(email);
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        usersRepository.findByEmail(email).ifPresent(users ->
                {
                        users.updateRefreshToken(refreshToken);
                        refreshTokenRepository.save(new RefreshTokenEntity(users));
                }
        );

        log.info( "로그인에 성공합니다. email: {}" , email);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}" ,accessToken);
        log.info( "RefreshTokenEntity 을 발급합니다. RefreshTokenEntity: {}" ,refreshToken);

        response.getWriter().write("success");
    }

    private String extractEmail(Authentication authentication) {
        UserDetails userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
