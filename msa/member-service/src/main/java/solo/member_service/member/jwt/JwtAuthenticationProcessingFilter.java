package solo.member_service.member.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import solo.member_service.member.entity.UserDetailsImpl;
import solo.member_service.member.entity.Users;
import solo.member_service.member.repository.RefreshTokenRepository;
import solo.member_service.member.repository.UsersRepository;

import java.io.IOException;

public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();//5

    private final String NO_CHECK_URL = "/signIn";//1

    public JwtAuthenticationProcessingFilter(JwtService jwtService, UsersRepository usersRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * 1. 리프레시 토큰이 오는 경우 -> 유효하면 AccessToken 재발급후, 필터 진행 X, 바로 튕기기
     *
     * 2. 리프레시 토큰은 없고 AccessToken만 있는 경우 -> 유저정보 저장후 필터 계속 진행
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;//안해주면 아래로 내려가서 계속 필터를 진행하게됨
        }

        String refreshToken = jwtService
                .extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null); //2

        if(refreshToken != null){
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);//3
            return;
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);//4
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).ifPresent(

                accessToken -> jwtService.extractEmail(accessToken).ifPresent(

                        email -> usersRepository.findByEmail(email).ifPresent(

                                users -> saveAuthentication(users)
                        )
                )
        );

        filterChain.doFilter(request,response);
    }

    private void saveAuthentication(Users users) {
        UserDetailsImpl userDetails = new UserDetailsImpl(users);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();//5
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        refreshTokenRepository.findById(refreshToken)
                .ifPresent(
                        tokenEntity -> {
                            jwtService.sendAccessToken(
                                    response,
                                    jwtService.createAccessToken(usersRepository.findById(tokenEntity.getMemberId()).orElseThrow().getEmail())
                            );
                        }
                );
    }
}
