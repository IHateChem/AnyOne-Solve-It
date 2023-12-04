package syleelsw.anyonesolveit.service.validation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter @ToString @NoArgsConstructor
public class UserSearchDto {
    private Long count;
    private List<ProblemDto> items;
    @Builder
    public UserSearchDto(Long count, List<ProblemDto> items) {
        this.count = count;
        this.items = items;
    }
}
