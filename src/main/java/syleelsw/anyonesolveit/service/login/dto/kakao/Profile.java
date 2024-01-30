package syleelsw.anyonesolveit.service.login.dto.kakao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter @ToString @Getter
public class Profile {
    private String nickname;
    private String thumbnail_image_url;
    private String profile_image_url;
    private Boolean is_default_image;
}
