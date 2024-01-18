package syleelsw.anyonesolveit.service.study.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.List;

@Getter
@NoArgsConstructor
public class StudyResponseMember {
    private Long id;
    private String username;
    private List<Integer> problems;

    public StudyResponseMember(UserInfo user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.problems = user.getSolvedProblem();
    }
}
