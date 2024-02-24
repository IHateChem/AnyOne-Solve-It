package syleelsw.anyonesolveit.service.validation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ValidateResponse {
    private String bjname;
    private boolean valid;
    @Builder
    public ValidateResponse(String bjname, boolean valid) {
        this.bjname = bjname;
        this.valid = valid;
    }
}
