package syleelsw.anyonesolveit.api.study.dto;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter @NoArgsConstructor
public class StudyDto {
    private String title;
    private String description;
    private String language;
    private String level;
    private String area;
    private String meeting_type;
    private String period;
    private String frequency;
    private String study_time;
    private List<String> members;
    @Builder
    private StudyDto(String title, String description, String language, String level, String area, String meeting_type, String period, String frequency, String study_time, List<String> members) {
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
    }
}
