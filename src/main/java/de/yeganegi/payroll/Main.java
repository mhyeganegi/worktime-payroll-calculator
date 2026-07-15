package de.yeganegi.payroll;

import de.yeganegi.payroll.calculation.WorkdayCalculator;
import de.yeganegi.payroll.model.WorkdayType;

import java.math.BigDecimal;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        WorkdayCalculator calculator = new WorkdayCalculator();
        BigDecimal workingHours = new BigDecimal("4.00");
        WorkdayType result = calculator.calculate(workingHours);

        System.out.printf(
                "Arbeitsstunden: %s | Bewertung: %s Arbeitstage (%s)%n",
                workingHours,
                result.getValue(),
                result
        );
    }
}
