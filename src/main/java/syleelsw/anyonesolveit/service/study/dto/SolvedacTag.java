package syleelsw.anyonesolveit.service.study.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class SolvedacTag {
    private String key;
    private boolean isMeta;
    private int bojTagId;
    private int problemCount;
    private List<DisplayName> displayNames;
    private List<Alias> aliases;
}
