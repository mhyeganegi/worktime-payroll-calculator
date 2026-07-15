package de.yeganegi.payroll.calculation;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GrossIncomeCalculatorTest {

    private final GrossIncomeCalculator calculator =
            new GrossIncomeCalculator();

    @Test
    void shouldCalculateGrossIncome() {
        BigDecimal result = calculator.calculate(
                new BigDecimal("8.00"),
                new BigDecimal("16.00")
        );

        assertEquals(new BigDecimal("128.00"), result);
    }

    @Test
    void shouldCalculateGrossIncomeWithDecimalHours() {
        BigDecimal result = calculator.calculate(
                new BigDecimal("3.50"),
                new BigDecimal("16.00")
        );

        assertEquals(new BigDecimal("56.00"), result);
    }

    @Test
    void shouldRejectNegativeWorkingHours() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(
                        new BigDecimal("-1.00"),
                        new BigDecimal("16.00")
                )
        );
    }

    @Test
    void shouldRejectNegativeHourlyWage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(
                        new BigDecimal("8.00"),
                        new BigDecimal("-16.00")
                )
        );
    }
}