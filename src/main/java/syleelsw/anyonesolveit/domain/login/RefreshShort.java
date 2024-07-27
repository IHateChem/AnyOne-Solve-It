package syleelsw.anyonesolveit.domain.login;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.http.HttpHeaders;

@Getter @Setter
@RedisHash(value ="short", timeToLive = 5) @ToString
public class RefreshShort {
    @Id
    private String refreshToken;
    private HttpHeaders jwtHeaders;
    public RefreshShort(String refreshToken,HttpHeaders jwtHeaders){
        this.refreshToken = refreshToken;
        this.jwtHeaders = jwtHeaders;
    }
}
