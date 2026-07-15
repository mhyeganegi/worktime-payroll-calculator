package de.yeganegi.payroll.time;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkingTimeCalculatorTest {

    private final WorkingTimeCalculator calculator =
            new WorkingTimeCalculator();

    @Test
    void shouldCalculateFourHoursAsHalfDayWorkingTime() {
        Duration duration = calculator.calculate(
                LocalTime.of(8, 0),
                LocalTime.of(12, 0),
                0
        );

        BigDecimal hours = TimeUtil.toHours(duration);

        assertEquals(new BigDecimal("4.00"), hours);
    }

    @Test
    void shouldCalculateThreeAndHalfHours() {
        Duration duration = calculator.calculate(
                LocalTime.of(8, 0),
                LocalTime.of(11, 30),
                0
        );

        BigDecimal hours = TimeUtil.toHours(duration);

        assertEquals(new BigDecimal("3.50"), hours);
    }

    @Test
    void shouldSubtractBreakTime() {
        Duration duration = calculator.calculate(
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                60
        );

        BigDecimal hours = TimeUtil.toHours(duration);

        assertEquals(new BigDecimal("8.00"), hours);
    }

    @Test
    void shouldCalculateShiftAcrossMidnight() {
        Duration duration = calculator.calculate(
                LocalTime.of(18, 0),
                LocalTime.of(2, 30),
                30
        );

        BigDecimal hours = TimeUtil.toHours(duration);

        assertEquals(new BigDecimal("8.00"), hours);
    }

    @Test
    void shouldRejectNegativeBreak() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(
                        LocalTime.of(8, 0),
                        LocalTime.of(17, 0),
                        -30
                )
        );
    }

    @Test
    void shouldRejectBreakLongerThanWorkingTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        90
                )
        );
    }
}