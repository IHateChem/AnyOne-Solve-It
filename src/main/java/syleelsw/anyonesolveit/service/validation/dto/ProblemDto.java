package syleelsw.anyonesolveit.service.validation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor

public class ProblemDto {
    private Integer problemId;
    private String titleKo;
    private Integer level;
    private List<TagDto> tags;
    @Builder
    private ProblemDto(Integer problemId, String titleKo, Integer level, List<TagDto> tags) {
        this.problemId = problemId;
        this.titleKo = titleKo;
        this.level = level;
        this.tags = tags;
    }
}
