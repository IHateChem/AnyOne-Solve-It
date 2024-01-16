package syleelsw.anyonesolveit.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.parameters.P;
import syleelsw.anyonesolveit.domain.study.Participation;
import syleelsw.anyonesolveit.domain.study.Repository.ParticipationRepository;
import syleelsw.anyonesolveit.domain.study.enums.ParticipationStates;

@Getter
@ToString
public class ParticipationResponse {
    private String participationId;
    private Long userId;
    private String message;
    private ParticipationStates state;
    public ParticipationResponse(Participation participation, Long id){
        this.participationId = participation.getId();
        this.userId = id;
        this.message = participation.getMessage();
        this.state = participation.getState();

    }
    @Builder
    public ParticipationResponse(String participationId, Long userId, String message, ParticipationStates state) {
        this.participationId = participationId;
        this.userId = userId;
        this.message = message;
        this.state = state;
    }


    public ParticipationResponse(){

    }
}
