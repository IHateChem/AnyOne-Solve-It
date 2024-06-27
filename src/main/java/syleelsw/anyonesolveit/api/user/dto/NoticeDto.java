package syleelsw.anyonesolveit.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.study.Notice;

import java.time.LocalDateTime;

@Getter @Setter @ToString
public class NoticeDto {
    private Long id;
    private Integer noticeType;
    private Long studyId;
    private String title;
    private String username;
    private Long userId;
    private LocalDateTime noticeTime;
    @Builder
    public NoticeDto(Long id, LocalDateTime noticeTime, Integer noticeType, Long studyId, String title, String username, Long userId) {
        this.id = id;
        this.noticeType = noticeType;
        this.noticeTime = noticeTime;
        this.studyId = studyId;
        this.title = title;
        this.username = username;
        this.userId = userId;
    }

    public static NoticeDto of(Notice notice){
        return builder()
                .id(notice.getId())
                .noticeType(notice.getNoticeType())
                .studyId(notice.getStudy().getId())
                .title(notice.getStudy().getTitle())
                .username(notice.getUser() == null ? null : notice.getUser().getName())
                .userId(notice.getUser() == null ? null : notice.getUser().getId())
                .noticeTime(notice.getModifiedDateTime())
                .build();
    }
}
