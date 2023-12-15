package syleelsw.anyonesolveit.service.login.dto.naver;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NaverRequest {
    private String client_id;
    //private String redirect_uri; //: 네이버 개발자센터에서 설정한 Callback URI
    private String client_secret;
    private String code;
    private String response_type;
    private String state;
}
