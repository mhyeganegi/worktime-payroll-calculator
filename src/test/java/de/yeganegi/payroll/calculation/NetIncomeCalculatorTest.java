package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.model.Deduction;
import de.yeganegi.payroll.model.DeductionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NetIncomeCalculatorTest {

    private final NetIncomeCalculator calculator =
            new NetIncomeCalculator();

    @Test
    void shouldCalculateTotalDeductions() {
        List<Deduction> deductions = List.of(
                new Deduction(
                        DeductionType.WAGE_TAX,
                        new BigDecimal("20.00")
                ),
                new Deduction(
                        DeductionType.HEALTH_INSURANCE,
                        new BigDecimal("15.00")
                ),
                new Deduction(
                        DeductionType.PENSION_INSURANCE,
                        new BigDecimal("10.00")
                )
        );

        BigDecimal result =
                calculator.calculateTotalDeductions(
                        deductions
                );

        assertEquals(
                new BigDecimal("45.00"),
                result
        );
    }

    @Test
    void shouldCalculateNetIncome() {
        List<Deduction> deductions = List.of(
                new Deduction(
                        DeductionType.WAGE_TAX,
                        new BigDecimal("20.00")
                ),
                new Deduction(
                        DeductionType.HEALTH_INSURANCE,
                        new BigDecimal("15.00")
                )
        );

        BigDecimal result =
                calculator.calculateNetIncome(
                        new BigDecimal("320.00"),
                        deductions
                );

        assertEquals(
                new BigDecimal("285.00"),
                result
        );
    }

    @Test
    void shouldCalculateNetIncomeWithoutDeductions() {
        BigDecimal result =
                calculator.calculateNetIncome(
                        new BigDecimal("320.00"),
                        List.of()
                );

        assertEquals(
                new BigDecimal("320.00"),
                result
        );
    }

    @Test
    void shouldRejectDeductionsHigherThanGrossIncome() {
        List<Deduction> deductions = List.of(
                new Deduction(
                        DeductionType.OTHER,
                        new BigDecimal("400.00")
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateNetIncome(
                        new BigDecimal("320.00"),
                        deductions
                )
        );
    }

    @Test
    void shouldRejectNegativeGrossIncome() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateNetIncome(
                        new BigDecimal("-1.00"),
                        List.of()
                )
        );
    }
}
