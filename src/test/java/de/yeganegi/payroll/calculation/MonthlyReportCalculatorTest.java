package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.EmploymentType;
import de.yeganegi.payroll.model.MonthlyReport;
import de.yeganegi.payroll.model.WorkEntry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MonthlyReportCalculatorTest {

    private final MonthlyReportCalculator calculator =
            new MonthlyReportCalculator();

    private final Employee employee =
            new Employee(
                    1,
                    "Hossein",
                    "Yeganegi",
                    true,
                    EmploymentType.WORKING_STUDENT,
                    new BigDecimal("16.00")
            );

    @Test
    void shouldCalculateMonthlyReport() {
        List<WorkEntry> entries = List.of(
                new WorkEntry(
                        LocalDate.of(2026, 7, 1),
                        LocalTime.of(8, 0),
                        LocalTime.of(12, 0),
                        0
                ),
                new WorkEntry(
                        LocalDate.of(2026, 7, 2),
                        LocalTime.of(8, 0),
                        LocalTime.of(17, 0),
                        60
                ),
                new WorkEntry(
                        LocalDate.of(2026, 7, 3),
                        LocalTime.of(18, 0),
                        LocalTime.of(2, 30),
                        30
                )
        );

        MonthlyReport report =
                calculator.calculate(
                        employee,
                        YearMonth.of(2026, 7),
                        entries
                );

        assertEquals(
                3,
                report.getWorkEntryCount()
        );

        assertEquals(
                new BigDecimal("20.00"),
                report.getTotalWorkingHours()
        );

        assertEquals(
                1,
                report.getHalfDays()
        );

        assertEquals(
                2,
                report.getFullDays()
        );

        assertEquals(
                new BigDecimal("2.5"),
                report.getTotalWorkdays()
        );

        assertEquals(
                new BigDecimal("320.00"),
                report.getGrossIncome()
        );
    }

    @Test
    void shouldCalculateEmptyMonth() {
        MonthlyReport report =
                calculator.calculate(
                        employee,
                        YearMonth.of(2026, 7),
                        List.of()
                );

        assertEquals(
                0,
                report.getWorkEntryCount()
        );

        assertEquals(
                BigDecimal.ZERO,
                report.getTotalWorkingHours()
        );

        assertEquals(
                new BigDecimal("0.00"),
                report.getGrossIncome()
        );
    }

    @Test
    void shouldRejectEntryFromDifferentMonth() {
        List<WorkEntry> entries = List.of(
                new WorkEntry(
                        LocalDate.of(2026, 8, 1),
                        LocalTime.of(8, 0),
                        LocalTime.of(12, 0),
                        0
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(
                        employee,
                        YearMonth.of(2026, 7),
                        entries
                )
        );
    }

    @Test
    void shouldRejectNullEmployee() {
        assertThrows(
                NullPointerException.class,
                () -> calculator.calculate(
                        null,
                        YearMonth.of(2026, 7),
                        List.of()
                )
        );
    }

    @Test
    void shouldRejectNullMonth() {
        assertThrows(
                NullPointerException.class,
                () -> calculator.calculate(
                        employee,
                        null,
                        List.of()
                )
        );
    }

    @Test
    void shouldRejectNullWorkEntries() {
        assertThrows(
                NullPointerException.class,
                () -> calculator.calculate(
                        employee,
                        YearMonth.of(2026, 7),
                        null
                )
        );
    }
}
