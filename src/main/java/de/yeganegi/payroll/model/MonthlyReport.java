package de.yeganegi.payroll.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;

public class MonthlyReport {

    private final long employeeId;
    private final YearMonth month;
    private final int workEntryCount;
    private final BigDecimal totalWorkingHours;
    private final BigDecimal totalWorkdays;
    private final long halfDays;
    private final long fullDays;
    private final BigDecimal grossIncome;

    public MonthlyReport(
            long employeeId,
            YearMonth month,
            int workEntryCount,
            BigDecimal totalWorkingHours,
            BigDecimal totalWorkdays,
            long halfDays,
            long fullDays,
            BigDecimal grossIncome
    ) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Mitarbeiter-ID muss größer als 0 sein."
            );
        }

        if (workEntryCount < 0 || halfDays < 0 || fullDays < 0) {
            throw new IllegalArgumentException(
                    "Anzahlen dürfen nicht negativ sein."
            );
        }

        this.employeeId = employeeId;
        this.month = Objects.requireNonNull(
                month,
                "Monat darf nicht null sein."
        );
        this.workEntryCount = workEntryCount;
        this.totalWorkingHours = Objects.requireNonNull(
                totalWorkingHours,
                "Gesamtstunden dürfen nicht null sein."
        );
        this.totalWorkdays = Objects.requireNonNull(
                totalWorkdays,
                "Arbeitstage dürfen nicht null sein."
        );
        this.halfDays = halfDays;
        this.fullDays = fullDays;
        this.grossIncome = Objects.requireNonNull(
                grossIncome,
                "Bruttoeinkommen darf nicht null sein."
        );
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public YearMonth getMonth() {
        return month;
    }

    public int getWorkEntryCount() {
        return workEntryCount;
    }

    public BigDecimal getTotalWorkingHours() {
        return totalWorkingHours;
    }

    public BigDecimal getTotalWorkdays() {
        return totalWorkdays;
    }

    public long getHalfDays() {
        return halfDays;
    }

    public long getFullDays() {
        return fullDays;
    }

    public BigDecimal getGrossIncome() {
        return grossIncome;
    }
}
