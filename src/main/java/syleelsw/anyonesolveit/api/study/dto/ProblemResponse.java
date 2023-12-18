package syleelsw.anyonesolveit.api.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter @ToString
public class ProblemResponse {
    private String title;
    private List<String> types;
    private String link;
    private Integer rank;
    @Builder
    private ProblemResponse(String title, List<String> types, String link, Integer rank) {
        this.title = title;
        this.types = types;
        this.link = link;
        this.rank = rank;
    }
    public static ProblemResponse of(SolvedacItem item){
        return builder()
                .title(item.getTitleKo())
                .types(item.getTags().stream().map(t -> t.getKey()).toList())
                .link("https://www.acmicpc.net/problem/"+item.getProblemId())
                .rank(item.getLevel())
                .build();
    }
}
