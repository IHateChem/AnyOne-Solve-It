package syleelsw.anyonesolveit.api.study.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ParticipationRequestDTO {
    private String participationId;
    private boolean confirm;

}
