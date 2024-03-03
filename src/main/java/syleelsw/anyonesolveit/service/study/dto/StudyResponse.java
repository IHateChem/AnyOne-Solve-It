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
    private Locations area;
    private String city;
    private String meeting_type;
    private String period;
    private String frequency;
    private String study_time;
    private Integer avg_rank;
    private Float avg_solved;
    private Integer how_many;
    private boolean recruiting;
    private boolean participated;

    private List<StudyResponseMember> members;
    private StudyResponseMember manager;
    @Builder
    private StudyResponse(Long id,Integer how_many, StudyResponseMember manager, Integer avg_rank,String openchat, boolean recruiting,  boolean participated,  Float avg_solved, String title, String city,  String description, LanguageTypes language, GoalTypes level, Locations area, String meeting_type, String period, String frequency, String study_time, List<StudyResponseMember> members) {
        this.id = id;
        this.how_many = how_many;
        this.manager = manager;
        this.openchat = openchat;
        this.avg_rank = avg_rank;
        this.avg_solved =avg_solved;
        this.city = city;
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
        this.recruiting = recruiting;
    }
    public static StudyResponse of(Study study){
        return builder()
                .id(study.getId())
                .how_many(study.getHow_many())
                .title(study.getTitle())
                .openchat(study.getOpenchat())
                .description(study.getDescription())
                .avg_rank(study.getAvg_rank())
                .avg_solved(study.getAvg_solved())
                .language(study.getLanguage())
                .level(study.getLevel())
                .area(study.getArea())
                .city(study.getCity())
                .meeting_type(study.getMeeting_type())
                .period(study.getPeriod())
                .frequency(study.getFrequency())
                .recruiting(study.getRecruiting())
                .study_time(study.getStudy_time())
                .manager(new StudyResponseMember(study.getUser()))
                .members(study.getMembers().stream().map(t -> new StudyResponseMember(t)).collect(Collectors.toList()))
                .build();
    }

    public static StudyResponse of(Study study, UserInfo userInfo){
        return builder()
                .id(study.getId())
                .title(study.getTitle())
                .description(study.getDescription())
                .manager(new StudyResponseMember(study.getUser()))
                .how_many(study.getHow_many())
                .openchat(study.getOpenchat())
                .avg_rank(study.getAvg_rank())
                .avg_solved(study.getAvg_solved())
                .language(study.getLanguage())
                .level(study.getLevel())
                .area(study.getArea())
                .city(study.getCity())
                .meeting_type(study.getMeeting_type())
                .recruiting(study.getRecruiting())
                .period(study.getPeriod())
                .frequency(study.getFrequency())
                .study_time(study.getStudy_time())
                .members(study.getMembers().stream().map(t -> new StudyResponseMember(t)).collect(Collectors.toList()))
                .participated(study.getMembers().contains(userInfo))
                .build();
    }
}
