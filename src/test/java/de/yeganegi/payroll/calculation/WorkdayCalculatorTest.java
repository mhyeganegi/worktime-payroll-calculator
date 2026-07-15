package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.model.WorkdayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkdayCalculatorTest {

    private WorkdayCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new WorkdayCalculator();
    }

    @Test
    void returnsNoneForZeroHours() {
        assertEquals(WorkdayType.NONE, calculator.calculate(new BigDecimal("0")));
    }

    @Test
    void returnsHalfDayForPositiveHoursBelowFour() {
        assertEquals(WorkdayType.HALF_DAY, calculator.calculate(new BigDecimal("3.5")));
    }

    @Test
    void returnsHalfDayForExactlyFourHours() {
        assertEquals(WorkdayType.HALF_DAY, calculator.calculate(new BigDecimal("4.00")));
    }

    @Test
    void returnsFullDayForMoreThanFourHours() {
        assertEquals(WorkdayType.FULL_DAY, calculator.calculate(new BigDecimal("4.01")));
    }

    @Test
    void rejectsNegativeHours() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(new BigDecimal("-1"))
        );
    }

    @Test
    void rejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(null));
    }
}
