package videoChat.solo.domain.users.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import videoChat.solo.domain.users.dto.UserDto;
import videoChat.solo.domain.users.entity.UserDetailsImpl;
import videoChat.solo.domain.users.entity.Users;
import videoChat.solo.domain.users.repository.UsersRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    public UserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Transactional
    public void save(UserDto dto) {
//        Optional.of(dto)
//                .filter(user -> user.getPassword().equals(user.getConfirmPassword()))
//                .orElse(dto1 -> usersRepository.save(new Users(dto1)))
//                .orElseThrow(new IllegalArgumentException("비밀번호가 다릅니다."));

        if(!dto.getPassword().equals(dto.getConfirmPassword())){
            throw new IllegalArgumentException("비밀번호가 다릅니다");
        }

        usersRepository.save(new Users(dto));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email));
        return new UserDetailsImpl(users);
    }
}
