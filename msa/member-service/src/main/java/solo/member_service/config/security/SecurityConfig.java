package solo.member_service.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import solo.member_service.config.security.handler.LoginFailureHandler;
import solo.member_service.config.security.handler.LoginSuccessJWTProvideHandler;
import solo.member_service.member.jwt.JwtAuthenticationProcessingFilter;
import solo.member_service.member.jwt.JwtService;
import solo.member_service.member.repository.RefreshTokenRepository;
import solo.member_service.member.repository.UsersRepository;
import solo.member_service.member.service.UserDetailsServiceImpl;

import java.util.function.Supplier;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;
    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    IpAddressMatcher hasIpAddress1 = new IpAddressMatcher(CONFIG);
    IpAddressMatcher hasIpAddress2 = new IpAddressMatcher(MEMBER);
    IpAddressMatcher hasIpAddress3 = new IpAddressMatcher(VIDEO);
    IpAddressMatcher hasIpAddress4 = new IpAddressMatcher(GATEWAY);
    IpAddressMatcher hasIpAddress5 = new IpAddressMatcher(NETWORK);
    IpAddressMatcher hasIpAddress6 = new IpAddressMatcher(NETWORK_GATEWAY);

    public static final String CONFIG = "172.19.0.2";
    public static final String MEMBER = "172.19.0.3";
    public static final String VIDEO = "172.19.0.4";
    public static final String GATEWAY = "172.19.0.5";
    public static final String NETWORK = "172.19.0.0";
    public static final String NETWORK_GATEWAY = "172.19.0.1";
    public static final String SUBNET = "/16";
    public static final IpAddressMatcher ALLOWED_IP_ADDRESS_MATCHER = new IpAddressMatcher(CONFIG + SUBNET);

    private final String[] allowedUrls = {
//            "/", "/main", "/signIn", "/signUp", "/register", "/login", "/h2-console/**", "/**"
            "/signIn", "/signUp", "/register", "/login"
    };

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, ObjectMapper objectMapper, UsersRepository usersRepository, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequest ->
                        authorizeRequest
                                .requestMatchers(allowedUrls)
                                .access((authentication, context) -> new AuthorizationDecision(hasIpAddress2.matches(context.getRequest())))
//                                .access(hasIpAddress(this::hasIpAddress))
//                                .permitAll() // 접근 제한 해제
                                .anyRequest().permitAll()
                )
                .logout((logout) -> logout
                        .logoutSuccessUrl("/signIn")
                        .invalidateHttpSession(true))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(
                        headersConfigurer ->
                                headersConfigurer
                                        .frameOptions(
                                                HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                        )
                )
                .addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class) // 추가 : 커스터마이징 된 필터를 SpringSecurityFilterChain에 등록
                .addFilterBefore(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    // 인증 관리자 관련 설정
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {//AuthenticationManager 등록
        DaoAuthenticationProvider provider = daoAuthenticationProvider();//DaoAuthenticationProvider 사용
//        provider.setPasswordEncoder(passwordEncoder());//PasswordEncoder로는 PasswordEncoderFactories.createDelegatingPasswordEncoder() 사용
        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
        return new LoginSuccessJWTProvideHandler(jwtService, usersRepository, refreshTokenRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() throws Exception {
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return jsonUsernamePasswordLoginFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter(){
        return new JwtAuthenticationProcessingFilter(jwtService, usersRepository, refreshTokenRepository);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 정적 리소스 spring security 대상에서 제외
        return (web) ->
                web
                        .ignoring()
                        .requestMatchers(
                                PathRequest.toStaticResources().atCommonLocations()
                        );
    }
}