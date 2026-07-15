package de.yeganegi.payroll;

import de.yeganegi.payroll.calculation.AutomaticDeductionCalculator;
import de.yeganegi.payroll.calculation.MonthlyReportCalculator;
import de.yeganegi.payroll.calculation.PayrollCalculator;
import de.yeganegi.payroll.model.Deduction;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.EmploymentType;
import de.yeganegi.payroll.model.MonthlyReport;
import de.yeganegi.payroll.model.Payroll;
import de.yeganegi.payroll.model.WorkEntry;
import de.yeganegi.payroll.repository.InMemoryWorkEntryRepository;
import de.yeganegi.payroll.repository.WorkEntryRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Employee employee = new Employee(
                1,
                "Hossein",
                "Yeganegi",
                true,
                EmploymentType.WORKING_STUDENT,
                new BigDecimal("16.00")
        );

        WorkEntryRepository repository =
                new InMemoryWorkEntryRepository();

        repository.save(
                employee.getId(),
                new WorkEntry(
                        LocalDate.of(2026, 7, 1),
                        LocalTime.of(8, 0),
                        LocalTime.of(12, 0),
                        0
                )
        );

        repository.save(
                employee.getId(),
                new WorkEntry(
                        LocalDate.of(2026, 7, 2),
                        LocalTime.of(8, 0),
                        LocalTime.of(17, 0),
                        60
                )
        );

        repository.save(
                employee.getId(),
                new WorkEntry(
                        LocalDate.of(2026, 7, 3),
                        LocalTime.of(18, 0),
                        LocalTime.of(2, 30),
                        30
                )
        );

        YearMonth month = YearMonth.of(2026, 7);

        List<WorkEntry> entries =
                repository.findByEmployeeAndMonth(
                        employee.getId(),
                        month
                );

        MonthlyReport monthlyReport =
                new MonthlyReportCalculator().calculate(
                        employee,
                        month,
                        entries
                );

        AutomaticDeductionCalculator deductionCalculator =
                new AutomaticDeductionCalculator();

        List<Deduction> deductions =
                deductionCalculator.calculate(
                        employee,
                        monthlyReport.getGrossIncome()
                );

        Payroll payroll =
                new PayrollCalculator().calculate(
                        employee,
                        monthlyReport,
                        deductions
                );

        System.out.println("----------------------------------------");
        System.out.println("Mitarbeiter     : " + employee.getFullName());
        System.out.println("Beschäftigung   : " + employee.getEmploymentType());
        System.out.println("Monat           : " + monthlyReport.getMonth());
        System.out.println("Schichten       : " + monthlyReport.getWorkEntryCount());
        System.out.println("Arbeitsstunden  : " + monthlyReport.getTotalWorkingHours());
        System.out.println("Arbeitstage     : " + monthlyReport.getTotalWorkdays());
        System.out.println("Brutto          : " + payroll.getGrossIncome() + " €");
        System.out.println();

        System.out.println("Geschätzte Abzüge:");

        for (Deduction deduction : payroll.getDeductions()) {
            System.out.println(
                    "- " + deduction.getType()
                            + ": "
                            + deduction.getAmount()
                            + " €"
            );
        }

        System.out.println();
        System.out.println(
                "Gesamtabzüge    : "
                        + payroll.getTotalDeductions()
                        + " €"
        );
        System.out.println(
                "Geschätztes Netto: "
                        + payroll.getNetIncome()
                        + " €"
        );
        System.out.println("----------------------------------------");
        System.out.println(
                "Hinweis: unverbindliche Schätzberechnung."
        );
    }
}
