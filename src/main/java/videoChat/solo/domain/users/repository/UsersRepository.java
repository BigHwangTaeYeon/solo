package videoChat.solo.domain.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import videoChat.solo.domain.users.entity.Users;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

//    Optional<Users> findByPhoneNum(String phoneNum);

//    boolean existsByPhoneNum(String phoneNum);

    Optional<Users> findByRefreshToken(String refreshToke);

}
