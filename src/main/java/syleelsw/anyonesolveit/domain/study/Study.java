package syleelsw.anyonesolveit.domain.study;

import jakarta.persistence.*;
import lombok.*;
import syleelsw.anyonesolveit.api.study.dto.StudyDto;
import syleelsw.anyonesolveit.domain.BaseEntity;
import syleelsw.anyonesolveit.domain.join.UserStudyJoin;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@ToString
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Study extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String language;
    private String level;
    private String area;
    private String meeting_type;
    private String period;
    private String frequency;
    private String study_time;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> members;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfo user;
    @Builder
    private Study(Long id, String title, String description,List<String> members, String language, String level, String area, String meeting_type, String period, String frequency, String study_time) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.language = language;
        this.level = level;
        this.area = area;
        this.members = members;
        this.meeting_type = meeting_type;
        this.period = period;
        this.frequency = frequency;
        this.study_time = study_time;
    }

    public static Study of(StudyDto studyDto) {
        return Study.builder()
                .title(studyDto.getTitle())
                .description(studyDto.getDescription())
                .language(studyDto.getLanguage())
                .level(studyDto.getLevel())
                .area(studyDto.getArea())
                .meeting_type(studyDto.getMeeting_type())
                .period(studyDto.getPeriod())
                .members(studyDto.getMembers())
                .frequency(studyDto.getFrequency())
                .study_time(studyDto.getStudy_time())
                .build();
    }
}
