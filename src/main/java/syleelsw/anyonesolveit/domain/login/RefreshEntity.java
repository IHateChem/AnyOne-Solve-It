package syleelsw.anyonesolveit.domain.login;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter @RedisHash(value ="refresh", timeToLive = 172800) @ToString
public class RefreshEntity {
    @Id
    private Long id;
    private String refreshToken;
    private Long expired;
    @TimeToLive
    private Long expiration;
    public RefreshEntity(Long id, String refreshToken){
        this.id = id;
        this.refreshToken = refreshToken;
        this.expired = System.currentTimeMillis() + 3600000 - 1000;
    }
}
