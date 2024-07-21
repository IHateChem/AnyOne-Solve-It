package syleelsw.anyonesolveit.api.study.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.study.ProblemCode;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProblemCodeDTO {
    private String name;
    private String code;
}
