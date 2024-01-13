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
        Set<UserInfo> members = study.getMembers();
    }
}
