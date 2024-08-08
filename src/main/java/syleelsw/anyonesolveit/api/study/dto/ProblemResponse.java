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
    private Long probNum;
    @Builder
    private ProblemResponse(Long problemNum, String title, List<String> types, String link, Integer rank) {
        this.title = title;
        this.probNum = problemNum;
        this.types = types;
        this.link = link;
        this.rank = rank;
    }
    public static ProblemResponse of(SolvedacItem item){
        return builder()
                .title(item.getTitleKo())
                .problemNum(item.getProblemId())
                .types(item.getTags().stream().map(t -> t.getKey()).toList())
                .link("https://www.acmicpc.net/problem/"+item.getProblemId())
                .rank(item.getLevel())
                .build();
    }

    public static ProblemResponse of(Problem item){
        return builder()
                .title(item.getTitle())
                .problemNum(item.getId())
                .types(item.getTypes())
                .link(item.getLink())
                .rank(item.getRank())
                .build();
    }

}
