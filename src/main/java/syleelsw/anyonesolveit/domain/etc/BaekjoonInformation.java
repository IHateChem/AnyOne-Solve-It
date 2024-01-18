package syleelsw.anyonesolveit.domain.etc;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Setter
public class BaekjoonInformation {
    @Id
    private String bjname;
    private Integer rank;
    private Long solved;
    @Builder
    public BaekjoonInformation(String bjname, Integer rank, Long solved) {
        this.bjname = bjname;
        this.rank = rank;
        this.solved = solved;
    }
}
