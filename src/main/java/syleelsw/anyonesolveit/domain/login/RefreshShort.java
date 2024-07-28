package syleelsw.anyonesolveit.domain.login;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.http.HttpHeaders;

@Getter @Setter
@RedisHash(value ="short", timeToLive = 10) @ToString
public class RefreshShort {
    @Id
    private String refreshToken;
    private String access;
    @TimeToLive
    private Long expiration;
    public RefreshShort(String refreshToken,String access){
        this.refreshToken = refreshToken;
        this.access = access;
    }
}
