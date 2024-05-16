package videoChat.solo.domain.users.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 300)
public class RefreshTokenEntity {

    @Id
    private final String refreshToken;
    private final Long memberId;

    public RefreshTokenEntity(final String refreshToken, final Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }

    public RefreshTokenEntity(Users users) {
        this.refreshToken = users.getRefreshToken();
        this.memberId = users.getId();
    }
}