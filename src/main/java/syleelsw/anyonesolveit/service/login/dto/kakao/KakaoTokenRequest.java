package syleelsw.anyonesolveit.service.login.dto.kakao;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Getter @Setter @ToString
public class KakaoTokenRequest {
    private String grant_type;
    private String client_id;
    private String redirect_uri;
    private String code;
    private String client_secret;
    @Builder
    public KakaoTokenRequest(String grant_type, String client_secret, String client_id, String redirect_uri, String code) {
        this.grant_type = grant_type;
        this.client_secret = client_secret;
        this.client_id = client_id;
        this.redirect_uri = redirect_uri;
        this.code = code;
    }

    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("grant_type", this.getGrant_type());
        map.add("client_id", this.getClient_id());
        map.add("redirect_uri", this.getRedirect_uri());
        map.add("code", this.getCode());
        map.add("client_secret", this.getClient_secret());
        return map;
    }
}
