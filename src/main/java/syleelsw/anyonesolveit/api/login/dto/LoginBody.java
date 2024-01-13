package syleelsw.anyonesolveit.api.login.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import jakarta.validation.constraints.NotNull;
import syleelsw.anyonesolveit.api.login.Provider;

@Getter @ToString
public class LoginBody {
    @NotNull
    private String authCode;
    private String authState;

    @NotNull
    private Provider provider;
    @Builder
    private LoginBody(String authCode, String authState, Provider provider) {
        this.authState = authState;
        this.authCode = authCode;
        this.provider = provider;
    }
}
