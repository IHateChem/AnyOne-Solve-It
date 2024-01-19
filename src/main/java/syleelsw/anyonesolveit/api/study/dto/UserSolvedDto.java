package syleelsw.anyonesolveit.api.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserSolvedDto {
    private String username;
    private List<Integer> problems;
    private Integer rank;
    @Builder
    public UserSolvedDto(String username, List<Integer> problems, Integer rank) {
        this.username = username;
        this.problems = problems;
        this.rank = rank;
    }
}
