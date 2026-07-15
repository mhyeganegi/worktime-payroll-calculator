package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.MonthlyReport;
import de.yeganegi.payroll.model.WorkEntry;
import de.yeganegi.payroll.model.WorkdayType;
import de.yeganegi.payroll.time.TimeUtil;
import de.yeganegi.payroll.time.WorkingTimeCalculator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

public class MonthlyReportCalculator {

    private final WorkingTimeCalculator workingTimeCalculator;
    private final WorkdayCalculator workdayCalculator;
    private final GrossIncomeCalculator grossIncomeCalculator;

    public MonthlyReportCalculator() {
        this.workingTimeCalculator =
                new WorkingTimeCalculator();
        this.workdayCalculator =
                new WorkdayCalculator();
        this.grossIncomeCalculator =
                new GrossIncomeCalculator();
    }

    public MonthlyReport calculate(
            Employee employee,
            YearMonth month,
            List<WorkEntry> workEntries
    ) {
        Objects.requireNonNull(employee);
        Objects.requireNonNull(month);
        Objects.requireNonNull(workEntries);

        BigDecimal totalHours = BigDecimal.ZERO;
        BigDecimal totalWorkdays = BigDecimal.ZERO;

        long halfDays = 0;
        long fullDays = 0;

        for (WorkEntry workEntry : workEntries) {
            YearMonth entryMonth =
                    YearMonth.from(workEntry.getDate());

            if (!entryMonth.equals(month)) {
                throw new IllegalArgumentException(
                        "Alle Arbeitseinträge müssen zum ausgewählten Monat gehören."
                );
            }

            Duration duration =
                    workingTimeCalculator.calculate(
                            workEntry.getStartTime(),
                            workEntry.getEndTime(),
                            workEntry.getBreakMinutes()
                    );

            BigDecimal hours =
                    TimeUtil.toHours(duration);

            WorkdayType workdayType =
                    workdayCalculator.calculate(hours);

            totalHours = totalHours.add(hours);
            totalWorkdays =
                    totalWorkdays.add(workdayType.getValue());

            if (workdayType == WorkdayType.HALF_DAY) {
                halfDays++;
            }

            if (workdayType == WorkdayType.FULL_DAY) {
                fullDays++;
            }
        }

        BigDecimal grossIncome =
                grossIncomeCalculator.calculate(
                        totalHours,
                        employee.getHourlyWage()
                );

        return new MonthlyReport(
                employee.getId(),
                month,
                workEntries.size(),
                totalHours,
                totalWorkdays,
                halfDays,
                fullDays,
                grossIncome
        );
    }
}