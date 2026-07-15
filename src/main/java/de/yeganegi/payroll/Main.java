package de.yeganegi.payroll;

import de.yeganegi.payroll.calculation.MonthlyReportCalculator;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.EmploymentType;
import de.yeganegi.payroll.model.MonthlyReport;
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

        List<WorkEntry> workEntries =
                repository.findByEmployeeAndMonth(
                        employee.getId(),
                        month
                );

        MonthlyReportCalculator calculator =
                new MonthlyReportCalculator();

        MonthlyReport report =
                calculator.calculate(
                        employee,
                        month,
                        workEntries
                );

        System.out.println("--------------------------------");
        System.out.println("Mitarbeiter    : " + employee.getFullName());
        System.out.println("Monat          : " + report.getMonth());
        System.out.println("Schichten      : " + report.getWorkEntryCount());
        System.out.println("Arbeitsstunden : " + report.getTotalWorkingHours());
        System.out.println("Halbe Tage     : " + report.getHalfDays());
        System.out.println("Volle Tage     : " + report.getFullDays());
        System.out.println("Arbeitstage    : " + report.getTotalWorkdays());
        System.out.println("Brutto         : " + report.getGrossIncome() + " €");
        System.out.println("--------------------------------");
    }
}