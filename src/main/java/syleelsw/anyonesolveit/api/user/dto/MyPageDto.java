package syleelsw.anyonesolveit.api.user.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.StaticValidator;

import java.util.List;
@NoArgsConstructor
@Getter
@ToString
public class MyPageDto {
    private String username;
    private String bjname;
    private String prefer_type;
    private String area;
    private String city;
    private LanguageTypes language;


    @Builder
    public MyPageDto(String username, String bjname, String prefer_type,String city, String area, LanguageTypes language) {
        this.username = username;
        this.bjname = bjname;
        this.prefer_type = prefer_type;
        this.city = city;
        this.area = area;
        this.language = language;
    }

    public static MyPageDto of(UserInfo userInfo){
        return builder()
                .username(userInfo.getName())
                .bjname(userInfo.getBjname())
                .language(userInfo.getLanguage())
                .prefer_type(userInfo.getPrefer_type())
                .area(userInfo.getArea().toString())
                .city(userInfo.getCity())
                .build();
    }

    @AssertTrue
    public boolean isValidArea(){
        return StaticValidator.isValidArea(area);
    }


}
