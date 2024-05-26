package solo.member_service.member.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import solo.member_service.member.entity.RefreshTokenEntity;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

//Redis Template 에서는 Repository 를 인터페이스로 정의하지 않고, 직접 아래와 같이 구현한다.
@Repository
public class RefreshTokenRepository {

    private final RedisTemplate<String, Long> redisTemplate;

    public RefreshTokenRepository(final RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(final RefreshTokenEntity refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getRefreshToken(), refreshToken.getMemberId());
        redisTemplate.expire(refreshToken.getRefreshToken(), 60L, TimeUnit.SECONDS);
    }

    public Optional<RefreshTokenEntity> findById(final String refreshToken) {
        ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
        Long memberId = valueOperations.get(refreshToken);

        if (Objects.isNull(memberId)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshTokenEntity(refreshToken, memberId));
    }

}
