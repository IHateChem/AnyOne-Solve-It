package syleelsw.anyonesolveit.domain.study;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import syleelsw.anyonesolveit.service.study.dto.Alias;
import syleelsw.anyonesolveit.service.study.dto.DisplayName;
import syleelsw.anyonesolveit.service.study.dto.SolvedacTag;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
public class ProblemTag {
    @Id
    private String problemKey;
    private boolean isMeta;
    private int bojTagId;
    private int problemCount;
    @Builder
    public ProblemTag(String key, boolean isMeta, int bojTagId, int problemCount) {
        this.problemKey = key;
        this.isMeta = isMeta;
        this.bojTagId = bojTagId;
        this.problemCount = problemCount;
    }
    public static ProblemTag of(SolvedacTag solvedacTag) {
        return ProblemTag.builder()
                .key(solvedacTag.getKey())
                .isMeta(solvedacTag.isMeta())
                .bojTagId(solvedacTag.getBojTagId())
                .problemCount(solvedacTag.getProblemCount())
                .build();
    }
}
