package syleelsw.anyonesolveit.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class MyPageResponse {
    private String username;
    private String bjname;
    private Integer rank;
    private String prefer_type;
    private Locations area;
    private String city;
    private List<LanguageTypes> languages;
    private Integer suggestions;
    private Integer changedParticipations;


    @Builder
    public MyPageResponse(String username, String bjname, Integer rank, String prefer_type, Locations area,String city, List<LanguageTypes> languages, Integer suggestions, Integer changedParticipations) {
        this.username = username;
        this.bjname = bjname;
        this.rank = rank;
        this.city = city;
        this.prefer_type = prefer_type;
        this.area = area;
        this.languages = languages;
        this.suggestions = suggestions;
        this.changedParticipations = changedParticipations;
    }

    public static MyPageResponse of(UserInfo userInfo, Integer suggestions, Integer changedParticipations){
        return builder()
            .suggestions(suggestions)
            .changedParticipations(changedParticipations)
            .username(userInfo.getUsername())
            .bjname(userInfo.getBjname())
            .rank(userInfo.getRank())
            .languages(userInfo.getLanguages())
            .prefer_type(userInfo.getPrefer_type())
            .area(userInfo.getArea())
            .city(userInfo.getCity())
            .city(userInfo.getCity())
            .build();
    }
}
