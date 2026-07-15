package de.yeganegi.payroll.time;

import java.time.Duration;
import java.time.LocalTime;

public class WorkingTimeCalculator {

    public Duration calculate(
            LocalTime start,
            LocalTime end,
            int breakMinutes
    ) {

        if (breakMinutes < 0) {
            throw new IllegalArgumentException("Pause darf nicht negativ sein.");
        }

        Duration duration;

        if (end.isBefore(start)) {

            duration =
                    Duration.between(start, LocalTime.MAX)
                            .plusSeconds(1)
                            .plus(Duration.between(LocalTime.MIN, end));

        } else {

            duration = Duration.between(start, end);

        }

        duration = duration.minusMinutes(breakMinutes);

        if (duration.isNegative()) {
            throw new IllegalArgumentException("Pause ist größer als Arbeitszeit.");
        }

        return duration;
    }
}