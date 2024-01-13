package syleelsw.anyonesolveit.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import syleelsw.anyonesolveit.api.user.dto.SolvedProblemDto;
import syleelsw.anyonesolveit.api.user.dto.UserProfileDto;
import syleelsw.anyonesolveit.domain.BaseEntity;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.etc.LanguageTypes;
import syleelsw.anyonesolveit.etc.Locations;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Entity @Getter @ToString(exclude = {"userStudyJoins"}) @NoArgsConstructor(access = AccessLevel.PROTECTED) @Setter
@Table(indexes = @Index(name = "idx_identifier", columnList = "identifier"))
public class UserInfo extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    private String username;
    private String email;
    private String identifier;
    private String bjname;
    private Integer rank;
    private String prefer_type;
    @Column(columnDefinition="TEXT")
    private String picture;
    @Enumerated(EnumType.STRING)
    private Locations area;
    @ElementCollection(fetch = FetchType.LAZY) @Enumerated(EnumType.STRING)
    private List<LanguageTypes> languages;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Integer> solvedProblem;
    @Column(columnDefinition="TEXT")
    private String solveProblemLevel;
    @ColumnDefault("false")
    private boolean isFirst;

    @ColumnDefault("0")
    private Integer solved;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Study> userStudyJoins  = new HashSet<>();

    @Builder
    private UserInfo(Long id, String username,String picture,  String email, String bjname, Integer rank, String prefer_type, Locations area, List<LanguageTypes> languages, boolean isFirst, List<Integer> solvedProblem, String solveProblemLevel,Integer solved, String identifier) {
        this.id = id;
        this.picture = picture;
        this.username = username;
        this.email = email;
        this.bjname = bjname;
        this.rank = rank;
        this.prefer_type = prefer_type;
        this.area = area;
        this.languages = languages;
        this.isFirst = isFirst;
        this.solvedProblem = solvedProblem;
        this.solveProblemLevel = solveProblemLevel;
        this.solved = solved;
        this.identifier = identifier;
    }
    public void update(Integer rank, SolvedProblemDto solvedProblem, UserProfileDto userProfile){
        this.rank = rank;
        this.solvedProblem = solvedProblem.getSolvedProblems();
        this.solveProblemLevel = solvedProblem.getSolved();
        this.bjname = userProfile.getBjname();
        this.prefer_type = userProfile.getPrefer_type();
        this.area = userProfile.getArea();
        this.languages = userProfile.getLanguages();
        this.isFirst = false;
        this.solved = getSolvedProblem().size();

    }
}
