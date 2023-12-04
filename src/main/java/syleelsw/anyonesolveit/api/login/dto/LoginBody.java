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

    @NotNull
    private Provider provider;
    @Builder
    private LoginBody(String authCode, Provider provider) {
        this.authCode = authCode;
        this.provider = provider;
    }
}
