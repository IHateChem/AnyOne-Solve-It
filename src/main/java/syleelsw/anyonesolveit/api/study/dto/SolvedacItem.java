package syleelsw.anyonesolveit.api.study.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter @ToString
public class SolvedacItem {
    private Long problemId;
    private String titleKo;
    private Integer level;
    private List<SolvedTag> tags;
}
