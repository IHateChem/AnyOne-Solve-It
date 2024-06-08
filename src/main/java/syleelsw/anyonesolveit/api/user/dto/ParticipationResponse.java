package syleelsw.anyonesolveit.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.parameters.P;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.study.Repository.ParticipationRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.study.enums.ParticipationStates;

@Getter
@ToString
public class ParticipationResponse {
    private String participationId;
    private Long userId;
    private String username;
    private String message;
    private ParticipationStates state;
    private String studyTitle;
    private Long studyId;
    public ParticipationResponse(Participation participation, Long id){
        this.participationId = participation.getId();
        this.username = participation.getUser().getName();
        this.userId = id;
        this.message = participation.getMessage();
        this.state = participation.getState();
        this.studyId = participation.getStudy().getId();
        this.studyTitle = participation.getStudy().getTitle();

    }
    @Builder
    public ParticipationResponse(String participationId, String userName, Long userId, String message, ParticipationStates state, String studyName, Long studyId) {
        this.participationId = participationId;
        this.userId = userId;
        this.username = userName;
        this.message = message;
        this.state = state;
        this.studyTitle = studyName;
        this.studyId = studyId;
    }


    public ParticipationResponse(){

    }
}
