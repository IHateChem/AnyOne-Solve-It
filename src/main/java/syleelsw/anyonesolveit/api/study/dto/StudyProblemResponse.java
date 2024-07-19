package syleelsw.anyonesolveit.api.study.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import syleelsw.anyonesolveit.domain.study.Problem;
import syleelsw.anyonesolveit.domain.study.StudyProblemEntity;

import java.util.List;

@Getter @Setter
public class StudyProblemResponse {
    private long probNum;
    private String title;
    private List<String> types;
    private String link;
    private Integer rank;
    @Builder
    public StudyProblemResponse(Long id, String title, List<String> types, String link, Integer rank) {
        this.probNum = id;
        this.title = title;
        this.types = types;
        this.link = link;
        this.rank = rank;
    }
    public static StudyProblemResponse of(StudyProblemEntity studyProblemEntity){
        Problem problem = studyProblemEntity.getProblem();
        return builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .types(problem.getTypes())
                .link(problem.getLink())
                .rank(problem.getRank())
                .build();
    }
}
