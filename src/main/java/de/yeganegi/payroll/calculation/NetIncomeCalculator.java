package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.model.Deduction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class NetIncomeCalculator {

    public BigDecimal calculateTotalDeductions(
            List<Deduction> deductions
    ) {
        Objects.requireNonNull(
                deductions,
                "Abzüge dürfen nicht null sein."
        );

        return deductions
                .stream()
                .map(Deduction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateNetIncome(
            BigDecimal grossIncome,
            List<Deduction> deductions
    ) {
        Objects.requireNonNull(
                grossIncome,
                "Bruttoeinkommen darf nicht null sein."
        );

        if (grossIncome.signum() < 0) {
            throw new IllegalArgumentException(
                    "Bruttoeinkommen darf nicht negativ sein."
            );
        }

        BigDecimal totalDeductions =
                calculateTotalDeductions(deductions);

        BigDecimal netIncome =
                grossIncome.subtract(totalDeductions);

        if (netIncome.signum() < 0) {
            throw new IllegalArgumentException(
                    "Abzüge dürfen das Bruttoeinkommen nicht überschreiten."
            );
        }

        return netIncome.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}
