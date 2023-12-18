package syleelsw.anyonesolveit.service.login.dto.naver;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NaverResponse {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private String expires_in;
}
