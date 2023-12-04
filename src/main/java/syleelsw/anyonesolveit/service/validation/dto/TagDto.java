package syleelsw.anyonesolveit.service.validation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor

public class TagDto {
    private String key;
    private Integer bojTagId;
    @Builder
    private TagDto(String key, Integer bojTagId) {
        this.key = key;
        this.bojTagId = bojTagId;
    }
}
