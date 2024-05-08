package videoChat.solo.domain.users.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import videoChat.solo.domain.users.dto.UserDto;
import videoChat.solo.domain.users.entity.UserDetailsImpl;
import videoChat.solo.domain.users.entity.Users;
import videoChat.solo.domain.users.repository.UsersRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    public UserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void save(UserDto dto) {
        usersRepository.save(new Users(dto));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(email));
        return new UserDetailsImpl(users);
    }
}