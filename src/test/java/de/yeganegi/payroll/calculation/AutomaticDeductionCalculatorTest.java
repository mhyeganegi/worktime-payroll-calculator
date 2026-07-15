package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.model.Deduction;
import de.yeganegi.payroll.model.DeductionType;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.EmploymentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AutomaticDeductionCalculatorTest {

    private final AutomaticDeductionCalculator calculator =
            new AutomaticDeductionCalculator();

    @Test
    void shouldCalculateEstimatedWorkingStudentDeductions() {
        Employee employee = createEmployee(
                EmploymentType.WORKING_STUDENT
        );

        List<Deduction> deductions = calculator.calculate(
                employee,
                new BigDecimal("1000.00")
        );

        assertEquals(2, deductions.size());

        assertEquals(
                new BigDecimal("50.00"),
                findAmount(deductions, DeductionType.WAGE_TAX)
        );

        assertEquals(
                new BigDecimal("93.00"),
                findAmount(
                        deductions,
                        DeductionType.PENSION_INSURANCE
                )
        );
    }

    @Test
    void shouldCalculateNoEstimatedEmployeeDeductionsForMiniJob() {
        Employee employee = createEmployee(
                EmploymentType.MINI_JOB
        );

        List<Deduction> deductions = calculator.calculate(
                employee,
                new BigDecimal("500.00")
        );

        assertEquals(0, deductions.size());
    }

    @Test
    void shouldCalculateEstimatedFullTimeDeductions() {
        Employee employee = createEmployee(
                EmploymentType.FULL_TIME
        );

        List<Deduction> deductions = calculator.calculate(
                employee,
                new BigDecimal("1000.00")
        );

        NetIncomeCalculator netIncomeCalculator =
                new NetIncomeCalculator();

        assertEquals(
                new BigDecimal("304.00"),
                netIncomeCalculator.calculateTotalDeductions(
                        deductions
                )
        );

        assertEquals(
                new BigDecimal("696.00"),
                netIncomeCalculator.calculateNetIncome(
                        new BigDecimal("1000.00"),
                        deductions
                )
        );
    }

    @Test
    void shouldRejectNegativeGrossIncome() {
        Employee employee = createEmployee(
                EmploymentType.WORKING_STUDENT
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(
                        employee,
                        new BigDecimal("-1.00")
                )
        );
    }

    private Employee createEmployee(
            EmploymentType employmentType
    ) {
        return new Employee(
                1,
                "Hossein",
                "Yeganegi",
                employmentType == EmploymentType.WORKING_STUDENT,
                employmentType,
                new BigDecimal("16.00")
        );
    }

    private BigDecimal findAmount(
            List<Deduction> deductions,
            DeductionType type
    ) {
        return deductions
                .stream()
                .filter(deduction ->
                        deduction.getType() == type
                )
                .findFirst()
                .orElseThrow()
                .getAmount();
    }
}
