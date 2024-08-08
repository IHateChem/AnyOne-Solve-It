package syleelsw.anyonesolveit.api.login.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class OtherProblemDTO {
    private String link;
    private List<String> types;
    private String title;
    private Integer rank;
}
