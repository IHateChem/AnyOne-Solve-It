package syleelsw.anyonesolveit.domain.study;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import syleelsw.anyonesolveit.api.study.dto.StudyDto;
import syleelsw.anyonesolveit.domain.BaseEntity;
import syleelsw.anyonesolveit.domain.join.UserStudyJoin;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.etc.GoalTypes;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@ToString(exclude = {"members", "user"})
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Study extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String openchat;
    @Enumerated(EnumType.STRING)
    private LanguageTypes language;
    @Enumerated(EnumType.STRING)
    private GoalTypes level;
    @Enumerated(EnumType.STRING)
    private Locations area;
    private String city;
    private String meeting_type;
    private String period;
    private String frequency;
    private String study_time;
    private Integer how_many;
    @ColumnDefault("0")
    private Long popularity;
    @ColumnDefault("0")
    private Integer avg_rank;
    @ColumnDefault("0")
    private Float avg_solved;

    //@ElementCollection(fetch = FetchType.LAZY)
    @ManyToMany
    @JoinTable(name = "study_member_join")
    private Set<UserInfo> members;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfo user;
    @Builder
    private Study(Long id,Integer how_many, String title, String description, String openchat, Set<UserInfo> members, LanguageTypes language, GoalTypes level, Locations area, String meeting_type, String period, String frequency, String study_time) {
        this.id = id;
        this.how_many = how_many;
        this.title = title;
        this.openchat = openchat;
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


    public static Study of(StudyDto studyDto, Set<UserInfo> members) {
        return Study.builder()
                .how_many(studyDto.getHow_many())
                .title(studyDto.getTitle())
                .description(studyDto.getDescription())
                .openchat(studyDto.getOpenchat())
                .language(studyDto.getLanguage())
                .level(studyDto.getLevel())
                .area(studyDto.getArea())
                .meeting_type(studyDto.getMeeting_type())
                .period(studyDto.getPeriod())
                .members(members)
                .frequency(studyDto.getFrequency())
                .study_time(studyDto.getStudy_time())
                .build();
    }
}
