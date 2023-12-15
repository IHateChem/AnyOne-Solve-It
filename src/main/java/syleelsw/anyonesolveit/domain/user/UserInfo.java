package syleelsw.anyonesolveit.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import syleelsw.anyonesolveit.domain.BaseEntity;
import syleelsw.anyonesolveit.domain.join.UserStudyJoin;
import syleelsw.anyonesolveit.domain.study.Study;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity @Getter @ToString(exclude = {"userStudyJoins"}) @NoArgsConstructor(access = AccessLevel.PROTECTED) @Setter
public class UserInfo extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    private String username;
    private String email;
    private String bjname;
    private Integer rank;
    private String prefer_type;
    private String area;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> languages;
    @ColumnDefault("false")
    private boolean isFirst;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Study> userStudyJoins  = new HashSet<>();

    @Builder
    private UserInfo(Long id, String username, String email, String bjname, Integer rank, String prefer_type, String area, List<String> languages, boolean isFirst) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bjname = bjname;
        this.rank = rank;
        this.prefer_type = prefer_type;
        this.area = area;
        this.languages = languages;
        this.isFirst = isFirst;
    }
}
