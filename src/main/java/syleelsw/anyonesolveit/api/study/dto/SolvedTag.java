package syleelsw.anyonesolveit.api.study.dto;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class SolvedTag {
    private String key;
    private Boolean isMeta;
    private Long bojTagId;
    private Integer problemCount;
}
