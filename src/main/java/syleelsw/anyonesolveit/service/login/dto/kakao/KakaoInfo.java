package syleelsw.anyonesolveit.service.login.dto.kakao;

import lombok.Data;

import java.util.Properties;

@Data
public class KakaoInfo {
    private Long id;
    private KakaoAccount kakao_account;
    private Properties properties;

    @Data
    public class Properties{
        private String nickname;
        private String profile_image;
        private String thumbnail_image;
    }
}
