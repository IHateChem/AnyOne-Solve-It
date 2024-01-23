package syleelsw.anyonesolveit.api.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import syleelsw.anyonesolveit.domain.user.UserInfo;

@Getter
@NoArgsConstructor
public class StudyMember {
    private String email;
    private Long userId;
    private String username;
    @Builder
    public StudyMember(String email, Long userId, String username) {
        this.email = email;
        this.userId = userId;
        this.username = username;
    }
    public static StudyMember fromUserInfo(UserInfo userInfo){
        return builder().userId(userInfo.getId()).email(userInfo.getEmail()).username(userInfo.getUsername()).build();
    }
}
