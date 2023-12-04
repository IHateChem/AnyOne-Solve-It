package syleelsw.anyonesolveit.service.login.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @ToString @NoArgsConstructor
public class LoginResponse {
    private String username;
    private Boolean isFirst;
    @Builder
    private LoginResponse(String username, Boolean isFirst) {
        this.username = username;
        this.isFirst = isFirst;
    }
}
