package syleelsw.anyonesolveit.api.study.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString @Setter
public class ParticipationDTO {
    private Long StudyId;
    //todo: message숫자 제한
    private String message;
    @Builder

    public ParticipationDTO(Long studyId, String message) {
        StudyId = studyId;
        this.message = message;
    }
}
