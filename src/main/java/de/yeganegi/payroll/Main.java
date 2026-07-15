package de.yeganegi.payroll;

import de.yeganegi.payroll.calculation.WorkdayCalculator;
import de.yeganegi.payroll.model.WorkdayType;
import de.yeganegi.payroll.time.TimeUtil;
import de.yeganegi.payroll.time.WorkingTimeCalculator;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {

        WorkingTimeCalculator calculator =
                new WorkingTimeCalculator();

        Duration duration =
                calculator.calculate(
                        LocalTime.of(18,0),
                        LocalTime.of(2,30),
                        30
                );

        BigDecimal hours =
                TimeUtil.toHours(duration);

        WorkdayCalculator workdayCalculator =
                new WorkdayCalculator();

        WorkdayType type =
                workdayCalculator.calculate(hours);

        System.out.println("-------------------------");
        System.out.println("Arbeitsstunden : " + hours);
        System.out.println("Arbeitstage    : " + type.getValue());
        System.out.println("-------------------------");
    }

}