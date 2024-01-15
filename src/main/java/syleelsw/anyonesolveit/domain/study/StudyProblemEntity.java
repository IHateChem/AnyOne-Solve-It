package syleelsw.anyonesolveit.domain.study;

import jakarta.persistence.*;
import lombok.*;
import syleelsw.anyonesolveit.domain.BaseEntity;
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyProblemEntity extends BaseEntity {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    private Problem problem;

    @Builder
    public StudyProblemEntity(String id, Study study, Problem problem) {
        this.id = id;
        this.study = study;
        this.problem = problem;
    }
}
