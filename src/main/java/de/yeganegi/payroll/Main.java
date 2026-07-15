package de.yeganegi.payroll;

import de.yeganegi.payroll.calculation.GrossIncomeCalculator;
import de.yeganegi.payroll.calculation.WorkdayCalculator;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.EmploymentType;
import de.yeganegi.payroll.model.WorkEntry;
import de.yeganegi.payroll.model.WorkdayType;
import de.yeganegi.payroll.time.TimeUtil;
import de.yeganegi.payroll.time.WorkingTimeCalculator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

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

        WorkEntry workEntry = new WorkEntry(
                LocalDate.of(2026, 7, 15),
                LocalTime.of(18, 0),
                LocalTime.of(2, 30),
                30
        );

        WorkingTimeCalculator workingTimeCalculator =
                new WorkingTimeCalculator();

        Duration duration = workingTimeCalculator.calculate(
                workEntry.getStartTime(),
                workEntry.getEndTime(),
                workEntry.getBreakMinutes()
        );

        BigDecimal workingHours = TimeUtil.toHours(duration);

        WorkdayCalculator workdayCalculator =
                new WorkdayCalculator();

        WorkdayType workdayType =
                workdayCalculator.calculate(workingHours);

        GrossIncomeCalculator grossIncomeCalculator =
                new GrossIncomeCalculator();

        BigDecimal grossIncome = grossIncomeCalculator.calculate(
                workingHours,
                employee.getHourlyWage()
        );

        System.out.println("--------------------------------");
        System.out.println("Mitarbeiter    : " + employee.getFullName());
        System.out.println("Student        : " + employee.isStudent());
        System.out.println("Beschäftigung  : " + employee.getEmploymentType());
        System.out.println("Datum          : " + workEntry.getDate());
        System.out.println("Arbeitsstunden : " + workingHours);
        System.out.println("Arbeitstage    : " + workdayType.getValue());
        System.out.println("Stundenlohn    : " + employee.getHourlyWage() + " €");
        System.out.println("Brutto         : " + grossIncome + " €");
        System.out.println("--------------------------------");
    }
}