package syleelsw.anyonesolveit.domain.join;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import syleelsw.anyonesolveit.domain.BaseEntity;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;

@Entity
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Setter
public class UserStudyJoin extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="study_id")
    private Study study;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserInfo user;
    @Builder
    private UserStudyJoin(Study study, UserInfo user) {
        this.study = study;
        this.user = user;
    }
}
