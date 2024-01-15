package syleelsw.anyonesolveit.api.study.dto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.etc.GoalTypes;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;

import java.util.List;
import java.util.Set;


@Getter @NoArgsConstructor
public class StudyDto {
    private String title;
    private String description;
    private LanguageTypes language;
    private GoalTypes level;
    private Locations area;
    private String city;
    private String meeting_type;
    private String period;
    private String frequency;
    private String study_time;
    private List<Long> members;
    @Builder
    private StudyDto(String title,String city,  String description, LanguageTypes language, GoalTypes level, Locations area, String meeting_type, String period, String frequency, String study_time, List<Long> members) {
        this.title = title;
        this.description = description;
        this.city = city;
        this.language = language;
        this.level = level;
        this.area = area;
        this.meeting_type = meeting_type;
        this.period = period;
        this.frequency = frequency;
        this.study_time = study_time;
        this.members = members;
    }
}
