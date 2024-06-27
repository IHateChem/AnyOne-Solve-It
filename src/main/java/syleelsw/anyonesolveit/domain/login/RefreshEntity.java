package syleelsw.anyonesolveit.domain.login;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter @RedisHash(value ="refresh", timeToLive = 172800) @ToString
public class RefreshEntity {
    @Id
    private Long id;
    private String refreshToken;
    private Long expired;
    public RefreshEntity(Long id, String refreshToken){
        this.id = id;
        this.refreshToken = refreshToken;
        this.expired = System.currentTimeMillis() + 180000 -1000; // 3600000 - 1000;
    }
}
