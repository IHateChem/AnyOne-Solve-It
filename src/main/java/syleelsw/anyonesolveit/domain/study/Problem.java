package syleelsw.anyonesolveit.domain.study;

import jakarta.persistence.*;
import lombok.*;
import syleelsw.anyonesolveit.api.study.dto.ProblemResponse;

import java.util.List;

@Entity
@Getter
@ToString
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Problem{
    @Id
    private Integer id;
    private String title;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> types;
    private String link;
    private Integer rank;
    @Builder
    public Problem(Integer problemId, String title, List<String> types, String link, Integer rank) {
        this.title = title;
        this.id = problemId;
        this.types = types;
        this.link = link;
        this.rank = rank;
    }

    public static Problem of(ProblemResponse problemResponse){
        return builder()
                .link(problemResponse.getLink())
                .problemId(problemResponse.getProbNum())
                .title(problemResponse.getTitle())
                .types(problemResponse.getTypes())
                .rank(problemResponse.getRank())
                .build();
    }

    public ProblemResponse toResponse(){
        return ProblemResponse.builder()
                .title(title)
                .types(types)
                .link(link)
                .rank(rank)
                .build();
    }
}