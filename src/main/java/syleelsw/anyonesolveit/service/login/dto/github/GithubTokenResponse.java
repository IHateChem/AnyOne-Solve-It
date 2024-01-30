package syleelsw.anyonesolveit.service.login.dto.github;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class GithubTokenResponse {
    private String access_token;
    private String token_type;
    private String scope;
}
