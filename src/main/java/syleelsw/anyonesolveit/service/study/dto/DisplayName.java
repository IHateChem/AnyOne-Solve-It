package syleelsw.anyonesolveit.service.study.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class DisplayName {
    private String language;
    private String name;
    @JsonProperty("short")
    private String probShort;
}
