package syleelsw.anyonesolveit.domain.login;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter @Setter
@RedisHash(value ="short", timeToLive = 5) @ToString
public class RefreshShort {
    @Id
    private String refreshToken;
    private String newRefresh;
    private String access;
    public RefreshShort(String refreshToken,String newRefresh ,String access){
        this.refreshToken = refreshToken;
        this.newRefresh = newRefresh;
        this.access = access;
    }
}
