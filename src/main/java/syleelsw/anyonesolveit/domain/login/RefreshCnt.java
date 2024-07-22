package syleelsw.anyonesolveit.domain.login;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter @Setter
@RedisHash(value ="short", timeToLive = 5) @ToString
public class RefreshCnt {
    @Id
    private String refreshToken;
    private int cnt;
    public RefreshCnt(String refreshToken, int cnt){
        this.refreshToken = refreshToken;
        this.cnt = cnt;
    }

    public void addCnt() {
        this.cnt++;
    }
}
