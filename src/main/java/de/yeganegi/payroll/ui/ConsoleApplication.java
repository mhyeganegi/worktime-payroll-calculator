package de.yeganegi.payroll.ui;

import de.yeganegi.payroll.calculation.AutomaticDeductionCalculator;
import de.yeganegi.payroll.database.DatabaseManager;
import de.yeganegi.payroll.calculation.MonthlyReportCalculator;
import de.yeganegi.payroll.calculation.PayrollCalculator;
import de.yeganegi.payroll.model.Deduction;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.EmploymentType;
import de.yeganegi.payroll.model.MonthlyReport;
import de.yeganegi.payroll.model.Payroll;
import de.yeganegi.payroll.model.WorkEntry;
import de.yeganegi.payroll.repository.EmployeeRepository;
import de.yeganegi.payroll.repository.SQLiteEmployeeRepository;
import de.yeganegi.payroll.repository.SQLiteWorkEntryRepository;
import de.yeganegi.payroll.repository.WorkEntryRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleApplication {

    private final Scanner scanner;
    private final WorkEntryRepository workEntryRepository;
    private final EmployeeRepository employeeRepository;
    private final MonthlyReportCalculator monthlyReportCalculator;
    private final AutomaticDeductionCalculator deductionCalculator;
    private final PayrollCalculator payrollCalculator;

    private Employee employee;

    public ConsoleApplication() {
        this.scanner = new Scanner(System.in);
        DatabaseManager.initializeDatabase();

        this.workEntryRepository =
                new SQLiteWorkEntryRepository();
        this.employeeRepository =
                new SQLiteEmployeeRepository();
        this.monthlyReportCalculator =
                new MonthlyReportCalculator();
        this.deductionCalculator =
                new AutomaticDeductionCalculator();
        this.payrollCalculator =
                new PayrollCalculator();
    }

    public void start() {
        printHeader();

        employee = employeeRepository
                .findById(1)
                .orElseGet(() -> {
                    Employee newEmployee =
                            createEmployee();

                    employeeRepository.save(
                            newEmployee
                    );

                    return newEmployee;
                });

        System.out.println();
        System.out.println(
                "Aktiver Mitarbeiter: "
                        + employee.getFullName()
        );

        boolean running = true;

        while (running) {
            printMenu();

            String selection = scanner.nextLine().trim();

            switch (selection) {
                case "1" -> addWorkEntry();
                case "2" -> showMonthlyPayroll();
                case "3" -> showEmployee();
                case "0" -> running = false;
                default -> System.out.println(
                        "Ungültige Auswahl. Bitte erneut versuchen."
                );
            }
        }

        System.out.println("Programm wurde beendet.");
    }

    private Employee createEmployee() {
        System.out.println();
        System.out.println("Mitarbeiter anlegen");

        String firstName =
                readRequiredText("Vorname: ");

        String lastName =
                readRequiredText("Nachname: ");

        BigDecimal hourlyWage =
                readPositiveDecimal("Stundenlohn: ");

        EmploymentType employmentType =
                readEmploymentType();

        boolean student =
                employmentType == EmploymentType.WORKING_STUDENT
                        || readYesNo("Ist die Person Student? (j/n): ");

        return new Employee(
                1,
                firstName,
                lastName,
                student,
                employmentType,
                hourlyWage
        );
    }

    private void addWorkEntry() {
        System.out.println();
        System.out.println("Neue Schicht erfassen");

        LocalDate date =
                readDate("Datum (JJJJ-MM-TT): ");

        LocalTime startTime =
                readTime("Startzeit (HH:MM): ");

        LocalTime endTime =
                readTime("Endzeit (HH:MM): ");

        int breakMinutes =
                readNonNegativeInteger(
                        "Pause in Minuten: "
                );

        WorkEntry workEntry = new WorkEntry(
                date,
                startTime,
                endTime,
                breakMinutes
        );

        workEntryRepository.save(
                employee.getId(),
                workEntry
        );

        System.out.println(
                "Schicht wurde erfolgreich gespeichert."
        );
    }

    private void showMonthlyPayroll() {
        System.out.println();
        System.out.println("Monatsabrechnung");

        YearMonth month =
                readYearMonth("Monat (JJJJ-MM): ");

        List<WorkEntry> workEntries =
                workEntryRepository
                        .findByEmployeeAndMonth(
                                employee.getId(),
                                month
                        );

        MonthlyReport monthlyReport =
                monthlyReportCalculator.calculate(
                        employee,
                        month,
                        workEntries
                );

        List<Deduction> deductions =
                deductionCalculator.calculate(
                        employee,
                        monthlyReport.getGrossIncome()
                );

        Payroll payroll =
                payrollCalculator.calculate(
                        employee,
                        monthlyReport,
                        deductions
                );

        printPayroll(monthlyReport, payroll);
    }

    private void showEmployee() {
        System.out.println();
        System.out.println("--------------------------------");
        System.out.println(
                "Name           : "
                        + employee.getFullName()
        );
        System.out.println(
                "Student        : "
                        + employee.isStudent()
        );
        System.out.println(
                "Beschäftigung  : "
                        + employee.getEmploymentType()
        );
        System.out.println(
                "Stundenlohn    : "
                        + employee.getHourlyWage()
                        + " €"
        );
        System.out.println("--------------------------------");
    }

    private void printPayroll(
            MonthlyReport monthlyReport,
            Payroll payroll
    ) {
        System.out.println();
        System.out.println("----------------------------------------");
        System.out.println(
                "Mitarbeiter      : "
                        + employee.getFullName()
        );
        System.out.println(
                "Monat            : "
                        + monthlyReport.getMonth()
        );
        System.out.println(
                "Schichten        : "
                        + monthlyReport.getWorkEntryCount()
        );
        System.out.println(
                "Arbeitsstunden   : "
                        + monthlyReport.getTotalWorkingHours()
        );
        System.out.println(
                "Halbe Tage       : "
                        + monthlyReport.getHalfDays()
        );
        System.out.println(
                "Volle Tage       : "
                        + monthlyReport.getFullDays()
        );
        System.out.println(
                "Arbeitstage      : "
                        + monthlyReport.getTotalWorkdays()
        );
        System.out.println(
                "Brutto           : "
                        + payroll.getGrossIncome()
                        + " €"
        );

        System.out.println();
        System.out.println("Geschätzte Abzüge:");

        if (payroll.getDeductions().isEmpty()) {
            System.out.println("- keine");
        } else {
            for (Deduction deduction
                    : payroll.getDeductions()) {
                System.out.println(
                        "- "
                                + deduction.getType()
                                + ": "
                                + deduction.getAmount()
                                + " €"
                );
            }
        }

        System.out.println();
        System.out.println(
                "Gesamtabzüge     : "
                        + payroll.getTotalDeductions()
                        + " €"
        );
        System.out.println(
                "Geschätztes Netto: "
                        + payroll.getNetIncome()
                        + " €"
        );
        System.out.println("----------------------------------------");
        System.out.println(
                "Hinweis: unverbindliche Schätzberechnung."
        );
    }

    private EmploymentType readEmploymentType() {
        while (true) {
            System.out.println();
            System.out.println("Beschäftigungsart:");
            System.out.println("1 - Werkstudent");
            System.out.println("2 - Minijob");
            System.out.println("3 - Midijob");
            System.out.println("4 - Teilzeit");
            System.out.println("5 - Vollzeit");

            System.out.print("Auswahl: ");

            String selection =
                    scanner.nextLine().trim();

            switch (selection) {
                case "1":
                    return EmploymentType.WORKING_STUDENT;
                case "2":
                    return EmploymentType.MINI_JOB;
                case "3":
                    return EmploymentType.MIDI_JOB;
                case "4":
                    return EmploymentType.PART_TIME;
                case "5":
                    return EmploymentType.FULL_TIME;
                default:
                    System.out.println(
                            "Ungültige Auswahl."
                    );
            }
        }
    }

    private String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);

            String value =
                    scanner.nextLine().trim();

            if (!value.isBlank()) {
                return value;
            }

            System.out.println(
                    "Eingabe darf nicht leer sein."
            );
        }
    }

    private BigDecimal readPositiveDecimal(
            String prompt
    ) {
        while (true) {
            System.out.print(prompt);

            String input = scanner
                    .nextLine()
                    .trim()
                    .replace(",", ".");

            try {
                BigDecimal value =
                        new BigDecimal(input);

                if (value.signum() >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println(
                    "Bitte eine gültige positive Zahl eingeben."
            );
        }
    }

    private int readNonNegativeInteger(
            String prompt
    ) {
        while (true) {
            System.out.print(prompt);

            try {
                int value =
                        Integer.parseInt(
                                scanner.nextLine().trim()
                        );

                if (value >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.println(
                    "Bitte eine ganze Zahl ab 0 eingeben."
            );
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return LocalDate.parse(
                        scanner.nextLine().trim()
                );
            } catch (DateTimeParseException exception) {
                System.out.println(
                        "Ungültiges Datum. Beispiel: 2026-07-15"
                );
            }
        }
    }

    private LocalTime readTime(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return LocalTime.parse(
                        scanner.nextLine().trim()
                );
            } catch (DateTimeParseException exception) {
                System.out.println(
                        "Ungültige Uhrzeit. Beispiel: 18:30"
                );
            }
        }
    }

    private YearMonth readYearMonth(
            String prompt
    ) {
        while (true) {
            System.out.print(prompt);

            try {
                return YearMonth.parse(
                        scanner.nextLine().trim()
                );
            } catch (DateTimeParseException exception) {
                System.out.println(
                        "Ungültiger Monat. Beispiel: 2026-07"
                );
            }
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);

            String input =
                    scanner.nextLine()
                            .trim()
                            .toLowerCase();

            if (input.equals("j")
                    || input.equals("ja")) {
                return true;
            }

            if (input.equals("n")
                    || input.equals("nein")) {
                return false;
            }

            System.out.println(
                    "Bitte j oder n eingeben."
            );
        }
    }

    private void printHeader() {
        System.out.println(
                "========================================"
        );
        System.out.println(
                "     Worktime Payroll Calculator"
        );
        System.out.println(
                "========================================"
        );
    }

    private void printMenu() {
        System.out.println();
        System.out.println("Hauptmenü");
        System.out.println("1 - Neue Schicht erfassen");
        System.out.println("2 - Monatsabrechnung anzeigen");
        System.out.println("3 - Mitarbeiter anzeigen");
        System.out.println("0 - Programm beenden");
        System.out.print("Auswahl: ");
    }
}
