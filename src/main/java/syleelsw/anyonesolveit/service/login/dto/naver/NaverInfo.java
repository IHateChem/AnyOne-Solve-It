package syleelsw.anyonesolveit.service.login.dto.naver;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NaverInfo {
    private String id;
    private String name;
    private String profile_image;
    private String age;
    private String gender;
    private String email;
    private String birthday;
    private String birthyear;
}
