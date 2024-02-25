package syleelsw.anyonesolveit.service.study.tools;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import syleelsw.anyonesolveit.domain.study.Repository.StudyRepository;
import syleelsw.anyonesolveit.domain.study.Study;
import syleelsw.anyonesolveit.domain.user.UserInfo;

import java.util.Set;

@Component
public class ProblemSolvedCountUpdater {
    public void update(Study study) {
        Long totalSolved = study.getMembers().stream().mapToLong(member -> member.getSolved()).sum();
        Integer totalRank = study.getMembers().stream().mapToInt(member -> member.getRank()).sum();
        study.setAvg_solved((float) (totalSolved/study.getMembers().size()));
        study.setAvg_rank( totalRank/study.getMembers().size());
    }
}
