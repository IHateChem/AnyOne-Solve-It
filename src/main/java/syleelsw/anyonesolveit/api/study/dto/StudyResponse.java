package syleelsw.anyonesolveit.api.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.etc.GoalTypes;
import syleelsw.anyonesolveit.etc.LanguageTypes;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class StudyResponse {
    private Long id;
    private String title;
    private String description;
    private Integer avg_rank;
    private Float avg_solved;
    private LanguageTypes language;
    private List<StudyMember> members;
    private List<UserSolvedDto> solved;
    private GoalTypes level;
    private String area;
    private String meeting_type;
    private String period;
    private String frequency;
    private String study_time;


    @Builder
    public StudyResponse(Long id, String title, String description, Integer avg_rank, Float avg_solved, LanguageTypes language, List<StudyMember> members, List<UserSolvedDto> solved, GoalTypes level, String area, String meeting_type, String period, String frequency, String study_time) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.avg_rank = avg_rank;
        this.avg_solved = avg_solved;
        this.language = language;
        this.members = members;
        this.solved = solved;
        this.level = level;
        this.area = area;
        this.meeting_type = meeting_type;
        this.period = period;
        this.frequency = frequency;
        this.study_time = study_time;
    }

    public static StudyResponse of(Study study){
        return builder()
                .id(study.getId())
                .title(study.getTitle())
                .description(study.getDescription())
                .avg_rank(study.getAvg_rank())
                .avg_solved(study.getAvg_solved())
                .language(study.getLanguage())
                .members(study.getMembers().stream().map(t -> StudyMember.fromUserInfo(t)).collect(Collectors.toList()))
                .solved(study.getMembers().stream().map(t ->
                        UserSolvedDto.builder().problems(t.getSolvedProblem()).username(t.getUsername()).rank(t.getRank()).build()).collect(Collectors.toList()))
                .level(study.getLevel())
                .area(study.getArea().toString()+" "+study.getCity())
                .meeting_type(study.getMeeting_type())
                .period(study.getPeriod())
                .frequency(study.getFrequency())
                .study_time(study.getStudy_time())
                .build();
    }
}
