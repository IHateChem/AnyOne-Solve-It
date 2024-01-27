package syleelsw.anyonesolveit.service.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.etc.GoalTypes;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@ToString
public class StudyResponse{
    private Long id;
    private String title;
    private String description;
    private LanguageTypes language;
    private String openchat;
    private GoalTypes level;
    private String area;
    private String meeting_type;
    private String period;
    private String frequency;
    private String study_time;
    private Integer avg_rank;
    private Float avg_solved;
    private boolean participated;

    private List<StudyResponseMember> members;
    @Builder
    private StudyResponse(Long id,Integer avg_rank,String openchat, boolean participated,  Float avg_solved, String title,String city,  String description, LanguageTypes language, GoalTypes level, String area, String meeting_type, String period, String frequency, String study_time, List<StudyResponseMember> members) {
        this.id = id;
        this.openchat = openchat;
        this.avg_rank = avg_rank;
        this.avg_solved =avg_solved;
        this.title = title;
        this.description = description;
        this.language = language;
        this.level = level;
        this.area = area;
        this.meeting_type = meeting_type;
        this.period = period;
        this.frequency = frequency;
        this.study_time = study_time;
        this.members = members;
        this.participated = participated;
    }
    public static StudyResponse of(Study study){
        return builder()
                .id(study.getId())
                .title(study.getTitle())
                .openchat(study.getOpenchat())
                .description(study.getDescription())
                .avg_rank(study.getAvg_rank())
                .avg_solved(study.getAvg_solved())
                .language(study.getLanguage())
                .level(study.getLevel())
                .area(study.getArea().toString()+" "+study.getCity())
                .meeting_type(study.getMeeting_type())
                .period(study.getPeriod())
                .frequency(study.getFrequency())
                .study_time(study.getStudy_time())
                .members(study.getMembers().stream().map(t -> new StudyResponseMember(t)).collect(Collectors.toList()))
                .build();
    }

    public static StudyResponse of(Study study, UserInfo userInfo){
        return builder()
                .id(study.getId())
                .title(study.getTitle())
                .description(study.getDescription())
                .openchat(study.getOpenchat())
                .avg_rank(study.getAvg_rank())
                .avg_solved(study.getAvg_solved())
                .language(study.getLanguage())
                .level(study.getLevel())
                .area(study.getArea().toString()+" "+study.getCity())
                .meeting_type(study.getMeeting_type())
                .period(study.getPeriod())
                .frequency(study.getFrequency())
                .study_time(study.getStudy_time())
                .members(study.getMembers().stream().map(t -> new StudyResponseMember(t)).collect(Collectors.toList()))
                .participated(study.getMembers().contains(userInfo))
                .build();
    }
}
