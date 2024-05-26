package solo.member_service.member.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.member_service.member.dto.UserDto;
import solo.member_service.member.entity.UserDetailsImpl;
import solo.member_service.member.entity.Users;
import solo.member_service.member.jwt.JwtServiceImpl;
import solo.member_service.member.repository.UsersRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final JwtServiceImpl jwtService;

    public UserDetailsServiceImpl(UsersRepository usersRepository, JwtServiceImpl jwtService) {
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public void save(UserDto dto) {
        Optional.of(dto)
                .filter(d -> d.getPassword().equals(d.getConfirmPassword()))
                .ifPresentOrElse(
                        d->usersRepository.save(new Users(d)),
                        () -> {
                            throw new IllegalArgumentException("비밀번호가 다릅니다.");
                        }
                );
    }

    public String emailByToken(String token) throws UsernameNotFoundException {
        return jwtService.extractEmail(token).orElseThrow(() -> new IllegalArgumentException("no token"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email));
        return new UserDetailsImpl(users);
    }
}
