package syleelsw.anyonesolveit.etc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SchedulerTest {

    @Mock
    private Clock clock;
    @Test
    public void testScheduledTask() {
        // 가짜 시계 생성
        Instant fixedInstant = Instant.parse("2024-01-01T23:59:40Z");
        Clock fixedClock = Clock.fixed(fixedInstant, ZoneId.systemDefault());
        LocalDateTime now = LocalDateTime.now();
        // Mockito를 사용하여 메서드 호출시 가짜 시계 반환하도록 설정
        Instant currentInstant = fixedInstant.plusSeconds(now.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(fixedInstant)));

        when(clock.instant()).thenReturn(currentInstant);
        //when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        // 이후에 테스트하고 싶은 코드 실행
        // 예: studyService.run() 등

        // 시간에 따른 동작 검증
        Instant actualInstant = Instant.now(clock);
        System.out.println(fixedClock);
        System.out.println(clock.instant());
    }
}
