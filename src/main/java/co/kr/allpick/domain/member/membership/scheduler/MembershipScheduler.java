package co.kr.allpick.domain.member.membership.scheduler;

import co.kr.allpick.domain.member.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MembershipScheduler {

    private static final Logger logger = LogManager.getLogger(MembershipScheduler.class);

    private final MembershipService membershipService;

    @Scheduled(cron = "0 5 0 1 * *")
    public void scheduleMembershipGradeUpdate() {
        logger.info("멤버십 등급 갱신 배치 시작");
        try {
            membershipService.updateAllMemberGrades();
        } catch (Exception e) {
            logger.error("멤버십 등급 갱신 배치 실패 - {}", e.getMessage(), e);
        }
    }
}