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
    private HttpHeaders jwtHeaders;
    @TimeToLive
    private Long expiration;
    public RefreshShort(String refreshToken,HttpHeaders jwtHeaders){
        this.refreshToken = refreshToken;
        this.jwtHeaders = jwtHeaders;
    }
}
