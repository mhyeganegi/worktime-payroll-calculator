package de.yeganegi.payroll.ui;

import de.yeganegi.payroll.calculation.AutomaticDeductionCalculator;
import de.yeganegi.payroll.calculation.MonthlyReportCalculator;
import de.yeganegi.payroll.calculation.PayrollCalculator;
import de.yeganegi.payroll.database.DatabaseManager;
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
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PayrollDesktopApplication extends Application {

    private static final long EMPLOYEE_ID = 1;

    private final EmployeeRepository employeeRepository =
            new SQLiteEmployeeRepository();

    private final WorkEntryRepository workEntryRepository =
            new SQLiteWorkEntryRepository();

    private final MonthlyReportCalculator monthlyReportCalculator =
            new MonthlyReportCalculator();

    private final AutomaticDeductionCalculator deductionCalculator =
            new AutomaticDeductionCalculator();

    private final PayrollCalculator payrollCalculator =
            new PayrollCalculator();

    private final TextField firstNameField =
            new TextField();

    private final TextField lastNameField =
            new TextField();

    private final TextField hourlyWageField =
            new TextField();

    private final CheckBox studentCheckBox =
            new CheckBox("Student");

    private final ComboBox<EmploymentType> employmentTypeBox =
            new ComboBox<>();

    private final DatePicker workDatePicker =
            new DatePicker(LocalDate.now());

    private final TextField startTimeField =
            new TextField("08:00");

    private final TextField endTimeField =
            new TextField("16:00");

    private final TextField breakMinutesField =
            new TextField("30");

    private final TextField reportMonthField =
            new TextField(
                    YearMonth.now().toString()
            );

    private final TextArea resultArea =
            new TextArea();

    @Override
    public void start(Stage stage) {
        DatabaseManager.initializeDatabase();

        employmentTypeBox.getItems().setAll(
                EmploymentType.values()
        );

        employmentTypeBox.setValue(
                EmploymentType.WORKING_STUDENT
        );

        resultArea.setEditable(false);
        resultArea.setPrefRowCount(15);

        loadEmployee();

        VBox root = new VBox(
                15,
                createTitle(),
                createEmployeeSection(),
                new Separator(),
                createWorkEntrySection(),
                new Separator(),
                createReportSection(),
                resultArea
        );

        root.setPadding(new Insets(20));

        Scene scene = new Scene(
                root,
                720,
                780
        );

        stage.setTitle(
                "Worktime Payroll Calculator"
        );

        stage.setScene(scene);
        stage.show();
    }

    private Label createTitle() {
        Label title =
                new Label(
                        "Worktime Payroll Calculator"
                );

        title.setStyle(
                "-fx-font-size: 24px;"
                        + "-fx-font-weight: bold;"
        );

        return title;
    }

    private GridPane createEmployeeSection() {
        GridPane grid = createGrid();

        grid.add(
                new Label("Vorname:"),
                0,
                0
        );

        grid.add(
                firstNameField,
                1,
                0
        );

        grid.add(
                new Label("Nachname:"),
                0,
                1
        );

        grid.add(
                lastNameField,
                1,
                1
        );

        grid.add(
                new Label("Stundenlohn:"),
                0,
                2
        );

        grid.add(
                hourlyWageField,
                1,
                2
        );

        grid.add(
                new Label("Beschäftigung:"),
                0,
                3
        );

        grid.add(
                employmentTypeBox,
                1,
                3
        );

        grid.add(
                studentCheckBox,
                1,
                4
        );

        Button saveButton =
                new Button(
                        "Mitarbeiter speichern"
                );

        saveButton.setOnAction(
                event -> saveEmployee()
        );

        grid.add(
                saveButton,
                1,
                5
        );

        return grid;
    }

    private GridPane createWorkEntrySection() {
        GridPane grid = createGrid();

        grid.add(
                new Label("Datum:"),
                0,
                0
        );

        grid.add(
                workDatePicker,
                1,
                0
        );

        grid.add(
                new Label("Startzeit:"),
                0,
                1
        );

        grid.add(
                startTimeField,
                1,
                1
        );

        grid.add(
                new Label("Endzeit:"),
                0,
                2
        );

        grid.add(
                endTimeField,
                1,
                2
        );

        grid.add(
                new Label("Pause in Minuten:"),
                0,
                3
        );

        grid.add(
                breakMinutesField,
                1,
                3
        );

        Button saveButton =
                new Button(
                        "Schicht speichern"
                );

        saveButton.setOnAction(
                event -> saveWorkEntry()
        );

        grid.add(
                saveButton,
                1,
                4
        );

        return grid;
    }

    private GridPane createReportSection() {
        GridPane grid = createGrid();

        grid.add(
                new Label("Monat:"),
                0,
                0
        );

        grid.add(
                reportMonthField,
                1,
                0
        );

        Button reportButton =
                new Button(
                        "Monatsabrechnung berechnen"
                );

        reportButton.setOnAction(
                event -> showMonthlyReport()
        );

        grid.add(
                reportButton,
                1,
                1
        );

        return grid;
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();

        grid.setHgap(12);
        grid.setVgap(10);

        return grid;
    }

    private void loadEmployee() {
        employeeRepository
                .findById(EMPLOYEE_ID)
                .ifPresent(employee -> {
                    firstNameField.setText(
                            employee.getFirstName()
                    );

                    lastNameField.setText(
                            employee.getLastName()
                    );

                    hourlyWageField.setText(
                            employee
                                    .getHourlyWage()
                                    .toPlainString()
                    );

                    employmentTypeBox.setValue(
                            employee.getEmploymentType()
                    );

                    studentCheckBox.setSelected(
                            employee.isStudent()
                    );
                });
    }

    private Employee readEmployee() {
        String firstName =
                firstNameField.getText().trim();

        String lastName =
                lastNameField.getText().trim();

        if (firstName.isBlank()
                || lastName.isBlank()) {
            throw new IllegalArgumentException(
                    "Vorname und Nachname sind erforderlich."
            );
        }

        String wageText =
                hourlyWageField
                        .getText()
                        .trim()
                        .replace(",", ".");

        BigDecimal hourlyWage =
                new BigDecimal(wageText);

        EmploymentType employmentType =
                employmentTypeBox.getValue();

        if (employmentType == null) {
            throw new IllegalArgumentException(
                    "Beschäftigungsart auswählen."
            );
        }

        return new Employee(
                EMPLOYEE_ID,
                firstName,
                lastName,
                studentCheckBox.isSelected(),
                employmentType,
                hourlyWage
        );
    }

    private void saveEmployee() {
        try {
            Employee employee = readEmployee();

            employeeRepository.save(employee);

            showInformation(
                    "Mitarbeiter wurde gespeichert."
            );
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void saveWorkEntry() {
        try {
            Employee employee =
                    employeeRepository
                            .findById(EMPLOYEE_ID)
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "Zuerst Mitarbeiter speichern."
                                            )
                            );

            LocalDate date =
                    workDatePicker.getValue();

            if (date == null) {
                throw new IllegalArgumentException(
                        "Datum auswählen."
                );
            }

            LocalTime startTime =
                    LocalTime.parse(
                            startTimeField
                                    .getText()
                                    .trim()
                    );

            LocalTime endTime =
                    LocalTime.parse(
                            endTimeField
                                    .getText()
                                    .trim()
                    );

            int breakMinutes =
                    Integer.parseInt(
                            breakMinutesField
                                    .getText()
                                    .trim()
                    );

            WorkEntry workEntry =
                    new WorkEntry(
                            date,
                            startTime,
                            endTime,
                            breakMinutes
                    );

            workEntryRepository.save(
                    employee.getId(),
                    workEntry
            );

            showInformation(
                    "Schicht wurde gespeichert."
            );
        } catch (DateTimeParseException exception) {
            showError(
                    "Uhrzeit muss beispielsweise 08:00 sein."
            );
        } catch (NumberFormatException exception) {
            showError(
                    "Pause muss eine ganze Zahl sein."
            );
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void showMonthlyReport() {
        try {
            Employee employee =
                    employeeRepository
                            .findById(EMPLOYEE_ID)
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "Zuerst Mitarbeiter speichern."
                                            )
                            );

            YearMonth month =
                    YearMonth.parse(
                            reportMonthField
                                    .getText()
                                    .trim()
                    );

            List<WorkEntry> entries =
                    workEntryRepository
                            .findByEmployeeAndMonth(
                                    employee.getId(),
                                    month
                            );

            MonthlyReport monthlyReport =
                    monthlyReportCalculator.calculate(
                            employee,
                            month,
                            entries
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

            resultArea.setText(
                    createReportText(
                            employee,
                            monthlyReport,
                            payroll
                    )
            );
        } catch (DateTimeParseException exception) {
            showError(
                    "Monat muss beispielsweise 2026-07 sein."
            );
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private String createReportText(
            Employee employee,
            MonthlyReport report,
            Payroll payroll
    ) {
        StringBuilder builder =
                new StringBuilder();

        builder.append("MONATSABRECHNUNG\n");
        builder.append("============================\n");
        builder.append("Mitarbeiter: ")
                .append(employee.getFullName())
                .append("\n");

        builder.append("Beschäftigung: ")
                .append(employee.getEmploymentType())
                .append("\n");

        builder.append("Monat: ")
                .append(report.getMonth())
                .append("\n\n");

        builder.append("Schichten: ")
                .append(report.getWorkEntryCount())
                .append("\n");

        builder.append("Arbeitsstunden: ")
                .append(report.getTotalWorkingHours())
                .append("\n");

        builder.append("Halbe Tage: ")
                .append(report.getHalfDays())
                .append("\n");

        builder.append("Volle Tage: ")
                .append(report.getFullDays())
                .append("\n");

        builder.append("Arbeitstage: ")
                .append(report.getTotalWorkdays())
                .append("\n\n");

        builder.append("Brutto: ")
                .append(payroll.getGrossIncome())
                .append(" €\n\n");

        builder.append("Geschätzte Abzüge:\n");

        if (payroll.getDeductions().isEmpty()) {
            builder.append("- keine\n");
        } else {
            for (Deduction deduction
                    : payroll.getDeductions()) {

                builder.append("- ")
                        .append(deduction.getType())
                        .append(": ")
                        .append(deduction.getAmount())
                        .append(" €\n");
            }
        }

        builder.append("\nGesamtabzüge: ")
                .append(payroll.getTotalDeductions())
                .append(" €\n");

        builder.append("Geschätztes Netto: ")
                .append(payroll.getNetIncome())
                .append(" €\n\n");

        builder.append(
                "Hinweis: unverbindliche Schätzberechnung."
        );

        return builder.toString();
    }

    private void showInformation(String message) {
        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle("Erfolg");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle("Fehler");
        alert.setHeaderText(
                "Eingabe konnte nicht verarbeitet werden."
        );

        alert.setContentText(
                message == null
                        ? "Unbekannter Fehler."
                        : message
        );

        alert.showAndWait();
    }
}
