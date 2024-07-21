package syleelsw.anyonesolveit.domain.study;

import jakarta.persistence.*;
import lombok.*;
import syleelsw.anyonesolveit.api.study.dto.ProblemResponse;

import java.util.List;

@Entity
@Getter
@ToString
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemDetail {
    @Id
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Study study;
    private Long problemNumber;
    @OneToMany(fetch = FetchType.LAZY)
    private List<ProblemCode> problemCodes;
    @Builder
    public ProblemDetail(Long problemNumber, List<ProblemCode> problemCodes) {
        this.problemNumber = problemNumber;
        this.problemCodes = problemCodes;
    }
}