package syleelsw.anyonesolveit.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.List;
@NoArgsConstructor @Getter @ToString
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String bjname;
    private String prefer_type;
    private String area;
    private List<String> languages;
    private boolean isFirst;
    @Builder
    private UserProfileDto(Long id, String username, String email, String bjname, String prefer_type, String area, List<String> languages, boolean isFirst) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bjname = bjname;
        this.prefer_type = prefer_type;
        this.area = area;
        this.languages = languages;
        this.isFirst = isFirst;
    }
    public void setId(Long id){
        this.id = id;
    }
    public void setFirst(Boolean isFirst){
        this.isFirst = isFirst;
    }

    public UserProfileDto of(UserInfo userInfo){
        return UserProfileDto.builder()
                .id(userInfo.getId())
                .username(userInfo.getUsername())
                .email(userInfo.getEmail())
                .bjname(userInfo.getBjname())
                .prefer_type(userInfo.getPrefer_type())
                .languages(userInfo.getLanguages())
                .isFirst(userInfo.isFirst())
                .build();
    }
    public UserInfo toUser(long id, Integer rank){
        return UserInfo.builder()
                .id(id)
                .username(this.username)
                .email(this.email)
                .bjname(this.bjname)
                .rank(rank)
                .prefer_type(this.prefer_type)
                .languages(this.languages)
                .isFirst(this.isFirst)
                .build();
    }
}
