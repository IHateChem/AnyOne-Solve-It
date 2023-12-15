package syleelsw.anyonesolveit.api.study.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Problem{
    private LocalDateTime expired;
    private String title;
    private List<String> types;
    private String link;
    private Integer rank;
    @Builder
    public Problem(LocalDateTime expired, String title, List<String> types, String link, Integer rank) {
        this.expired = expired;
        this.title = title;
        this.types = types;
        this.link = link;
        this.rank = rank;
    }

    public static Problem of(ProblemResponse problemResponse){
        return builder()
                .expired(LocalDateTime.now().plusMinutes(5))
                .link(problemResponse.getLink())
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