package syleelsw.anyonesolveit.service.login.dto.naver;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NaverInfoResponse {
    private String resultcode;
    private String message;
    private NaverInfo response;
}
