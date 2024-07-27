package syleelsw.anyonesolveit.api.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder @Getter
public class SolvedProblemDto{
    private List<Long> solvedProblems;
    private String solved;
}