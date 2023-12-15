package syleelsw.anyonesolveit.api.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class SolvedacUserInfoDto {
    private Long solvedCount;
    @JsonProperty("class")
    private Long classRank;
    @JsonProperty("tier")
    private Integer rank;
}
