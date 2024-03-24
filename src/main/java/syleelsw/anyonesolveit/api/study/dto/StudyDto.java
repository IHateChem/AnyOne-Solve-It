package syleelsw.anyonesolveit.api.study.dto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import syleelsw.anyonesolveit.domain.user.UserInfo;
import syleelsw.anyonesolveit.etc.GoalTypes;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;

import java.util.List;
import java.util.Set;



@Getter @NoArgsConstructor
public class StudyDto {
    @Size(max= 15) //maxTitle
    private String title;
    @Size(max= 40) //maxDescription
    private String description;
    private LanguageTypes language;
    private GoalTypes level;
    private Locations area;
    private String city;
    private String meeting_type;
    private String period;
    private String frequency;
    private Integer how_many;
    private Boolean recruiting = true;
    @Size(max=50)
    private String study_time;
    @Size(max=30)
    private List<Long> members;
    @Size(max=500)
    private String openchat;
    @Builder
    private StudyDto(String title,Integer how_many,Boolean recruiting,  String city, String openchat,  String description, LanguageTypes language, GoalTypes level, Locations area, String meeting_type, String period, String frequency, String study_time, List<Long> members) {
        this.title = title;
        this.how_many = how_many;
        this.recruiting = recruiting;
        this.description = description;
        this.openchat = openchat;
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
