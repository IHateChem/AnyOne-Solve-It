package syleelsw.anyonesolveit.service.login.dto.github;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter @ToString
public class GithubTokenRequest {
    private String client_id;
    private String client_secret;
    private String code;
}
