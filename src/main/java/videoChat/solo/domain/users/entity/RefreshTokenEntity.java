package videoChat.solo.domain.users.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 800)
public class RefreshTokenEntity {

    @Id
    private String refreshToken;
    private Long memberId;

    public RefreshTokenEntity(final String refreshToken, final Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }

}