package syleelsw.anyonesolveit.api.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import syleelsw.anyonesolveit.domain.study.Notice;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter @ToString
public class NoticeResponse {
    private List<NoticeDto> notices;

    @Builder
    public NoticeResponse(List<Notice> notices) {
        this.notices = notices.stream().map(NoticeDto::of).collect(Collectors.toList());
    }
}
