package syleelsw.anyonesolveit.service.study.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class SolvedacTagDTO {
    private int count;
    private List<SolvedacTag> items;

}
