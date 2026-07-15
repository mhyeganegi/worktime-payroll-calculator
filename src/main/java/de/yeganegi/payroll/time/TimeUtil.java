package de.yeganegi.payroll.time;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public final class TimeUtil {

    private TimeUtil() {}

    public static BigDecimal toHours(Duration duration) {

        BigDecimal minutes =
                BigDecimal.valueOf(duration.toMinutes());

        return minutes
                .divide(BigDecimal.valueOf(60),2,RoundingMode.HALF_UP);
    }

}