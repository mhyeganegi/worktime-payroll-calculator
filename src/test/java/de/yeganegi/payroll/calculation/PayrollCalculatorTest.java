package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.model.Deduction;
import de.yeganegi.payroll.model.DeductionType;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.EmploymentType;
import de.yeganegi.payroll.model.MonthlyReport;
import de.yeganegi.payroll.model.Payroll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayrollCalculatorTest {

    private final PayrollCalculator calculator =
            new PayrollCalculator();

    private final Employee employee =
            new Employee(
                    1,
                    "Hossein",
                    "Yeganegi",
                    true,
                    EmploymentType.WORKING_STUDENT,
                    new BigDecimal("16.00")
            );

    private final MonthlyReport monthlyReport =
            new MonthlyReport(
                    1,
                    YearMonth.of(2026, 7),
                    3,
                    new BigDecimal("20.00"),
                    new BigDecimal("2.5"),
                    1,
                    2,
                    new BigDecimal("320.00")
            );

    @Test
    void shouldCalculatePayroll() {
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

        Payroll payroll = calculator.calculate(
                employee,
                monthlyReport,
                deductions
        );

        assertEquals(
                new BigDecimal("320.00"),
                payroll.getGrossIncome()
        );

        assertEquals(
                new BigDecimal("45.00"),
                payroll.getTotalDeductions()
        );

        assertEquals(
                new BigDecimal("275.00"),
                payroll.getNetIncome()
        );

        assertEquals(
                3,
                payroll.getDeductions().size()
        );
    }

    @Test
    void shouldRejectReportFromDifferentEmployee() {
        MonthlyReport otherReport =
                new MonthlyReport(
                        2,
                        YearMonth.of(2026, 7),
                        0,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        0,
                        0,
                        BigDecimal.ZERO
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(
                        employee,
                        otherReport,
                        List.of()
                )
        );
    }
}
