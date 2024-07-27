package syleelsw.anyonesolveit.api.study.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ProblemSearchDTO {
    private String query;
    private boolean notSolved;
}
