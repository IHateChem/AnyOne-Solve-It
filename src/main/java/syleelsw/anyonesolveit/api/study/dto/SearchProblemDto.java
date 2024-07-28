package syleelsw.anyonesolveit.api.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.study.Problem;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.service.validation.dto.TagDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter @NoArgsConstructor @ToString
public class SearchProblemDto {
    private boolean isExist;
    private List<StudyMember> whoSolved;
    private Long problemId;
    private String title;
    private int rank;
    private List<String> types;
    @Builder
    public SearchProblemDto(boolean isExist,String title,Long problemId, List<StudyMember> whoSolved, int rank, List<String> types) {
        this.types = types;
        this.title = title;
        this.rank = rank;
        this.problemId = problemId;
        this.isExist = isExist;
        this.whoSolved = whoSolved;
    }
    public static SearchProblemDto of(Study study, SolvedacItem item){
        Long problemId = item.getProblemId();
        int rank = item.getLevel();
        List<String> types = item.getTags().stream().map(SolvedTag::getKey).collect(Collectors.toList());
        String title = item.getTitleKo();
        return builder()
                .problemId(problemId)
                .isExist(true)
                .title(title)
                .whoSolved(study.getMembers().stream().filter(t-> t.getSolvedProblem().contains(problemId)).map(t2-> StudyMember.fromUserInfo(t2)).collect(Collectors.toList()))
                .types(types)
                .rank(rank)
                .build();

    }

    public static SearchProblemDto of(boolean isExist, Study study, Problem problem){
        if (problem==null) {
            return builder().isExist(false).build();
        }
        Long problemId = problem.getId();
        int rank = problem.getRank();
        List<String> types = problem.getTypes();
        return builder()
                .isExist(isExist)
                .title(problem.getTitle())
                .whoSolved(study.getMembers().stream().filter(t-> t.getSolvedProblem().contains(problemId)).map(t2-> StudyMember.fromUserInfo(t2)).collect(Collectors.toList()))
                .types(types)
                .rank(rank)
                .build();

    }
}
