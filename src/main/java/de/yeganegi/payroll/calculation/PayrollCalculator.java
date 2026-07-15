package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.model.Deduction;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.MonthlyReport;
import de.yeganegi.payroll.model.Payroll;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class PayrollCalculator {

    private final NetIncomeCalculator netIncomeCalculator;

    public PayrollCalculator() {
        this.netIncomeCalculator =
                new NetIncomeCalculator();
    }

    public Payroll calculate(
            Employee employee,
            MonthlyReport monthlyReport,
            List<Deduction> deductions
    ) {
        Objects.requireNonNull(
                employee,
                "Mitarbeiter darf nicht null sein."
        );

        Objects.requireNonNull(
                monthlyReport,
                "Monatsbericht darf nicht null sein."
        );

        Objects.requireNonNull(
                deductions,
                "Abzüge dürfen nicht null sein."
        );

        if (employee.getId()
                != monthlyReport.getEmployeeId()) {
            throw new IllegalArgumentException(
                    "Monatsbericht gehört nicht zum Mitarbeiter."
            );
        }

        BigDecimal totalDeductions =
                netIncomeCalculator
                        .calculateTotalDeductions(deductions);

        BigDecimal netIncome =
                netIncomeCalculator.calculateNetIncome(
                        monthlyReport.getGrossIncome(),
                        deductions
                );

        return new Payroll(
                employee.getId(),
                monthlyReport.getMonth(),
                monthlyReport.getGrossIncome(),
                deductions,
                totalDeductions,
                netIncome
        );
    }
}
