package syleelsw.anyonesolveit.service.study.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.List;

@Getter
@NoArgsConstructor @ToString
public class StudyResponseMember {
    private Long userId;
    private String username;
    private int rank;
    //private List<Integer> problems;

    public StudyResponseMember(UserInfo user){
        this.userId = user.getId();
        this.username = user.getName();
        this.rank = user.getRank();
        //this.problems = user.getSolvedProblem();
    }
}
