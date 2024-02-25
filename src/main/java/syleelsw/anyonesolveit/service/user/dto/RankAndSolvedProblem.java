package syleelsw.anyonesolveit.service.user.dto;

import syleelsw.anyonesolveit.api.user.dto.SolvedProblemDto;

public class RankAndSolvedProblem{
    public Integer rank;
    public SolvedProblemDto solvedProblemDto;

    public RankAndSolvedProblem(Integer rank, SolvedProblemDto solvedProblemDto) {
        this.rank = rank;
        this.solvedProblemDto = solvedProblemDto;
    }
}