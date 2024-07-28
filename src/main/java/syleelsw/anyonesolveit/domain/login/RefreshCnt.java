package syleelsw.anyonesolveit.domain.login;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter @Setter
@RedisHash(value ="short", timeToLive = 5) @ToString
@Slf4j
public class RefreshCnt {
    @Id
    private String refreshToken;
    private Integer cnt;
    @TimeToLive
    private Long expiration;
    public RefreshCnt(String refreshToken, int cnt){
        this.refreshToken = refreshToken;
        this.cnt = cnt;
    }

    public void addCnt() {
        log.info("refresh cnt add,id:{} cnt : {} ",this.refreshToken, this.cnt);
        this.cnt++;
    }
}
