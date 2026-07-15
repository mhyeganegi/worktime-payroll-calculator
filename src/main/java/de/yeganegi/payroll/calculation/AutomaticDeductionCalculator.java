package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.config.EstimatedDeductionProfile;
import de.yeganegi.payroll.config.EstimatedProfileFactory;
import de.yeganegi.payroll.model.Deduction;
import de.yeganegi.payroll.model.DeductionType;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.PayrollProfile;
import de.yeganegi.payroll.model.TaxClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutomaticDeductionCalculator {

    private static final BigDecimal CHURCH_TAX_RATE =
            new BigDecimal("0.09");

    public List<Deduction> calculate(
            Employee employee,
            BigDecimal grossIncome
    ) {
        EstimatedDeductionProfile baseProfile =
                EstimatedProfileFactory.create(
                        employee.getEmploymentType()
                );

        PayrollProfile defaultProfile =
                new PayrollProfile(
                        employee.getId(),
                        TaxClass.I,
                        baseProfile.healthInsuranceRate(),
                        false
                );

        return calculate(
                employee,
                grossIncome,
                defaultProfile
        );
    }

    public List<Deduction> calculate(
            Employee employee,
            BigDecimal grossIncome,
            PayrollProfile payrollProfile
    ) {
        Objects.requireNonNull(
                employee,
                "Mitarbeiter darf nicht null sein."
        );

        Objects.requireNonNull(
                grossIncome,
                "Bruttoeinkommen darf nicht null sein."
        );

        Objects.requireNonNull(
                payrollProfile,
                "Abrechnungsprofil darf nicht null sein."
        );

        if (grossIncome.signum() < 0) {
            throw new IllegalArgumentException(
                    "Bruttoeinkommen darf nicht negativ sein."
            );
        }

        if (employee.getId()
                != payrollProfile.employeeId()) {
            throw new IllegalArgumentException(
                    "Abrechnungsprofil gehört nicht zum Mitarbeiter."
            );
        }

        EstimatedDeductionProfile baseProfile =
                EstimatedProfileFactory.create(
                        employee.getEmploymentType()
                );

        List<Deduction> deductions =
                new ArrayList<>();

        BigDecimal estimatedWageTaxRate =
                baseProfile
                        .wageTaxRate()
                        .multiply(
                                taxClassFactor(
                                        payrollProfile.taxClass()
                                )
                        );

        BigDecimal wageTax =
                calculateAmount(
                        grossIncome,
                        estimatedWageTaxRate
                );

        addAmountDeduction(
                deductions,
                DeductionType.WAGE_TAX,
                wageTax
        );

        addRateDeduction(
                deductions,
                DeductionType.HEALTH_INSURANCE,
                grossIncome,
                payrollProfile.healthInsuranceRate()
        );

        addRateDeduction(
                deductions,
                DeductionType.PENSION_INSURANCE,
                grossIncome,
                baseProfile.pensionInsuranceRate()
        );

        addRateDeduction(
                deductions,
                DeductionType.NURSING_CARE_INSURANCE,
                grossIncome,
                baseProfile.nursingCareInsuranceRate()
        );

        addRateDeduction(
                deductions,
                DeductionType.UNEMPLOYMENT_INSURANCE,
                grossIncome,
                baseProfile.unemploymentInsuranceRate()
        );

        if (payrollProfile.churchTaxEnabled()
                && wageTax.signum() > 0) {

            BigDecimal churchTax =
                    wageTax
                            .multiply(CHURCH_TAX_RATE)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            addAmountDeduction(
                    deductions,
                    DeductionType.CHURCH_TAX,
                    churchTax
            );
        }

        return List.copyOf(deductions);
    }

    private BigDecimal taxClassFactor(
            TaxClass taxClass
    ) {
        return switch (taxClass) {
            case I -> new BigDecimal("1.00");
            case II -> new BigDecimal("0.85");
            case III -> new BigDecimal("0.60");
            case IV -> new BigDecimal("1.00");
            case V -> new BigDecimal("1.35");
            case VI -> new BigDecimal("1.60");
        };
    }

    private void addRateDeduction(
            List<Deduction> deductions,
            DeductionType type,
            BigDecimal grossIncome,
            BigDecimal rate
    ) {
        addAmountDeduction(
                deductions,
                type,
                calculateAmount(
                        grossIncome,
                        rate
                )
        );
    }

    private BigDecimal calculateAmount(
            BigDecimal grossIncome,
            BigDecimal rate
    ) {
        return grossIncome
                .multiply(rate)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private void addAmountDeduction(
            List<Deduction> deductions,
            DeductionType type,
            BigDecimal amount
    ) {
        if (amount.signum() == 0) {
            return;
        }

        deductions.add(
                new Deduction(
                        type,
                        amount
                )
        );
    }
}
