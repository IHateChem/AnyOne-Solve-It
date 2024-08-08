package syleelsw.anyonesolveit.api.study.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class SolvedProblemPages {
    private Integer count;
    private List<SolvedacItem> items;
}
