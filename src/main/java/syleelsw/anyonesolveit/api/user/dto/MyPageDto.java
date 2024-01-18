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
    private List<LanguageTypes> languages;


    @Builder
    public MyPageDto(String username, String bjname, String prefer_type, String area, List<LanguageTypes> languages) {
        this.username = username;
        this.bjname = bjname;
        this.prefer_type = prefer_type;
        this.area = area;
        this.languages = languages;
    }

    public static MyPageDto of(UserInfo userInfo){
        return builder()
                .username(userInfo.getUsername())
                .bjname(userInfo.getBjname())
                .languages(userInfo.getLanguages())
                .prefer_type(userInfo.getPrefer_type())
                .area(userInfo.getArea().toString() + " " + userInfo.getCity())
                .build();
    }

    @AssertTrue
    public boolean isValidArea(){
        return StaticValidator.isValidArea(area);
    }


}
