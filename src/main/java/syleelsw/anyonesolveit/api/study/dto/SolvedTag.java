package syleelsw.anyonesolveit.api.study.dto;

import lombok.Getter;
import lombok.ToString;
import syleelsw.anyonesolveit.service.study.dto.Alias;
import syleelsw.anyonesolveit.service.study.dto.DisplayName;

import java.util.List;

@Getter @ToString
public class SolvedTag {
    private String key;
    private Boolean isMeta;
    private Long bojTagId;
    private Integer problemCount;
    private List<DisplayName> displayNames;
    private List<Alias> aliases;
    private Object metadata;


}
