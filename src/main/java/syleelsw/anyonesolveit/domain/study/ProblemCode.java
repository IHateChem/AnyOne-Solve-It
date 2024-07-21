package syleelsw.anyonesolveit.domain.study;

import jakarta.persistence.*;
import lombok.*;
import syleelsw.anyonesolveit.api.study.dto.ProblemResponse;
import syleelsw.anyonesolveit.domain.BaseEntity;

import java.util.List;

@Entity
@Getter
@ToString
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemCode extends BaseEntity {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    @Builder
    public ProblemCode(String name, String code) {
        this.name = name;
        this.code = code;
    }

}