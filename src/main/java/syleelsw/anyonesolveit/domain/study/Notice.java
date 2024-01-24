package syleelsw.anyonesolveit.domain.study;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import syleelsw.anyonesolveit.domain.user.UserInfo;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) @Table(indexes = @Index(name = "to_index", columnList = "to_user_id"))
public class Notice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Integer noticeType;
    @ManyToOne(fetch = FetchType.LAZY) @NotNull
    private Study study;
    @ManyToOne(fetch = FetchType.LAZY)
    private UserInfo user;
    @ManyToOne(fetch = FetchType.LAZY) @NotNull
    private UserInfo toUser;
    @Builder
    public Notice(Integer noticeType, Study study, UserInfo user, UserInfo toUser) {
        this.noticeType = noticeType;
        this.study = study;
        this.user = user;
        this.toUser = toUser;
    }
}
