package syleelsw.anyonesolveit.service.study.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.List;

@Getter
@NoArgsConstructor @ToString
public class StudyResponseMember {
    private Long id;
    private String username;
    private int rank;
    //private List<Integer> problems;

    public StudyResponseMember(UserInfo user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.rank = user.getRank();
        //this.problems = user.getSolvedProblem();
    }
}
