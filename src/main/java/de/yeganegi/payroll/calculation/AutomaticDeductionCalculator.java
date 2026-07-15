package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.config.EstimatedDeductionProfile;
import de.yeganegi.payroll.config.EstimatedProfileFactory;
import de.yeganegi.payroll.model.Deduction;
import de.yeganegi.payroll.model.DeductionType;
import de.yeganegi.payroll.model.Employee;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutomaticDeductionCalculator {

    public List<Deduction> calculate(
            Employee employee,
            BigDecimal grossIncome
    ) {
        Objects.requireNonNull(
                employee,
                "Mitarbeiter darf nicht null sein."
        );

        Objects.requireNonNull(
                grossIncome,
                "Bruttoeinkommen darf nicht null sein."
        );

        if (grossIncome.signum() < 0) {
            throw new IllegalArgumentException(
                    "Bruttoeinkommen darf nicht negativ sein."
            );
        }

        EstimatedDeductionProfile profile =
                EstimatedProfileFactory.create(
                        employee.getEmploymentType()
                );

        List<Deduction> deductions = new ArrayList<>();

        addDeduction(
                deductions,
                DeductionType.WAGE_TAX,
                grossIncome,
                profile.wageTaxRate()
        );

        addDeduction(
                deductions,
                DeductionType.HEALTH_INSURANCE,
                grossIncome,
                profile.healthInsuranceRate()
        );

        addDeduction(
                deductions,
                DeductionType.PENSION_INSURANCE,
                grossIncome,
                profile.pensionInsuranceRate()
        );

        addDeduction(
                deductions,
                DeductionType.NURSING_CARE_INSURANCE,
                grossIncome,
                profile.nursingCareInsuranceRate()
        );

        addDeduction(
                deductions,
                DeductionType.UNEMPLOYMENT_INSURANCE,
                grossIncome,
                profile.unemploymentInsuranceRate()
        );

        return List.copyOf(deductions);
    }

    private void addDeduction(
            List<Deduction> deductions,
            DeductionType type,
            BigDecimal grossIncome,
            BigDecimal rate
    ) {
        if (rate.signum() == 0) {
            return;
        }

        BigDecimal amount = grossIncome
                .multiply(rate)
                .setScale(2, RoundingMode.HALF_UP);

        deductions.add(
                new Deduction(type, amount)
        );
    }
}
