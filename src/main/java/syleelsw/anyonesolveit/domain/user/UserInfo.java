package syleelsw.anyonesolveit.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import syleelsw.anyonesolveit.domain.BaseEntity;

import java.util.List;

@Entity @Getter @ToString @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
