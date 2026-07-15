package de.yeganegi.payroll.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

public class Payroll {

    private final long employeeId;
    private final YearMonth month;
    private final BigDecimal grossIncome;
    private final List<Deduction> deductions;
    private final BigDecimal totalDeductions;
    private final BigDecimal netIncome;

    public Payroll(
            long employeeId,
            YearMonth month,
            BigDecimal grossIncome,
            List<Deduction> deductions,
            BigDecimal totalDeductions,
            BigDecimal netIncome
    ) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Mitarbeiter-ID muss größer als 0 sein."
            );
        }

        this.month = Objects.requireNonNull(
                month,
                "Monat darf nicht null sein."
        );

        this.grossIncome = requireNonNegative(
                grossIncome,
                "Bruttoeinkommen"
        );

        this.deductions = List.copyOf(
                Objects.requireNonNull(
                        deductions,
                        "Abzüge dürfen nicht null sein."
                )
        );

        this.totalDeductions = requireNonNegative(
                totalDeductions,
                "Gesamtabzüge"
        );

        this.netIncome = requireNonNegative(
                netIncome,
                "Nettoeinkommen"
        );

        this.employeeId = employeeId;
    }

    private BigDecimal requireNonNegative(
            BigDecimal value,
            String fieldName
    ) {
        Objects.requireNonNull(
                value,
                fieldName + " darf nicht null sein."
        );

        if (value.signum() < 0) {
            throw new IllegalArgumentException(
                    fieldName + " darf nicht negativ sein."
            );
        }

        return value.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    public long getEmployeeId() {
        return employeeId;
    }

    public YearMonth getMonth() {
        return month;
    }

    public BigDecimal getGrossIncome() {
        return grossIncome;
    }

    public List<Deduction> getDeductions() {
        return deductions;
    }

    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }

    public BigDecimal getNetIncome() {
        return netIncome;
    }
}
