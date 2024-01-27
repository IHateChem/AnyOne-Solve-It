package syleelsw.anyonesolveit.api.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.study.Study;

import java.util.List;
import java.util.stream.Collectors;

@Getter @NoArgsConstructor @ToString
public class SearchProblemDto {
    private boolean isExist;
    private List<StudyMember> whoSolved;
    @Builder
    public SearchProblemDto(boolean isExist, List<StudyMember> whoSolved) {
        this.isExist = isExist;
        this.whoSolved = whoSolved;
    }

    public static SearchProblemDto of(boolean isExist, Study study, Integer problemId){
        return builder()
                .isExist(isExist)
                .whoSolved(study.getMembers().stream().filter(t-> t.getSolvedProblem().contains(problemId)).map(t2-> StudyMember.fromUserInfo(t2)).collect(Collectors.toList()))
                .build();
    }
}
