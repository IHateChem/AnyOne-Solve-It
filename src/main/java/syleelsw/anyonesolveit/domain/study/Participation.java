package syleelsw.anyonesolveit.domain.study;

import jakarta.persistence.*;
import lombok.*;
import syleelsw.anyonesolveit.api.study.dto.ParticipationDTO;
import syleelsw.anyonesolveit.domain.BaseEntity;
import syleelsw.anyonesolveit.domain.study.enums.ParticipationStates;
import syleelsw.anyonesolveit.domain.user.UserInfo;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participation extends BaseEntity {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfo user;

    private String message;

    @Enumerated(EnumType.STRING)
    private ParticipationStates state;
    @Builder
    public Participation(String id, Study study, UserInfo user, String message, ParticipationStates state) {
        this.id = id;
        this.study = study;
        this.user = user;
        this.message = message;
        this.state = state;
    }
}
