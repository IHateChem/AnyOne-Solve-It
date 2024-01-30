package syleelsw.anyonesolveit.api.study.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString @Setter
public class ParticipationDTO {
    private Long StudyId;
    @Size(max= 500) //maxDescription
    private String message;
    @Builder

    public ParticipationDTO(Long studyId, String message) {
        StudyId = studyId;
        this.message = message;
    }
}
