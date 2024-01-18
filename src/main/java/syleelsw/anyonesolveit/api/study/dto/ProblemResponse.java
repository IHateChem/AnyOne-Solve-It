package syleelsw.anyonesolveit.api.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.study.Problem;

import java.util.List;

@Getter @ToString
public class ProblemResponse {
    private String title;
    private List<String> types;
    private String link;
    private Integer rank;
    private Integer problemId;
    @Builder
    private ProblemResponse(Integer problemId, String title, List<String> types, String link, Integer rank) {
        this.title = title;
        this.problemId = problemId;
        this.types = types;
        this.link = link;
        this.rank = rank;
    }
    public static ProblemResponse of(SolvedacItem item){
        return builder()
                .title(item.getTitleKo())
                .problemId(item.getProblemId())
                .types(item.getTags().stream().map(t -> t.getKey()).toList())
                .link("https://www.acmicpc.net/problem/"+item.getProblemId())
                .rank(item.getLevel())
                .build();
    }

    public static ProblemResponse of(Problem item){
        return builder()
                .title(item.getTitle())
                .problemId(Math.toIntExact(item.getId()))
                .types(item.getTypes())
                .link(item.getLink())
                .rank(item.getRank())
                .build();
    }

}
