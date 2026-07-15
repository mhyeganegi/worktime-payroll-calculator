package de.yeganegi.payroll.ui;

import de.yeganegi.payroll.calculation.AutomaticDeductionCalculator;
import de.yeganegi.payroll.calculation.MonthlyReportCalculator;
import de.yeganegi.payroll.calculation.PayrollCalculator;
import de.yeganegi.payroll.database.DatabaseManager;
import de.yeganegi.payroll.export.ReportExportService;
import de.yeganegi.payroll.model.Deduction;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.EmploymentType;
import de.yeganegi.payroll.model.MonthlyReport;
import de.yeganegi.payroll.model.Payroll;
import de.yeganegi.payroll.model.StoredWorkEntry;
import de.yeganegi.payroll.model.WorkEntry;
import de.yeganegi.payroll.repository.EmployeeRepository;
import de.yeganegi.payroll.repository.SQLiteEmployeeRepository;
import de.yeganegi.payroll.repository.SQLiteWorkEntryRepository;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class PayrollDesktopApplication
        extends Application {

    private static final long EMPLOYEE_ID = 1;

    private final EmployeeRepository employeeRepository =
            new SQLiteEmployeeRepository();

    private final SQLiteWorkEntryRepository workEntryRepository =
            new SQLiteWorkEntryRepository();

    private final MonthlyReportCalculator monthlyReportCalculator =
            new MonthlyReportCalculator();

    private final AutomaticDeductionCalculator deductionCalculator =
            new AutomaticDeductionCalculator();

    private final PayrollCalculator payrollCalculator =
            new PayrollCalculator();

    private final ReportExportService reportExportService =
            new ReportExportService();

    private Employee lastEmployee;
    private MonthlyReport lastMonthlyReport;
    private Payroll lastPayroll;
    private List<StoredWorkEntry> lastStoredEntries;
    private String lastReportText;

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

    private final TableView<StoredWorkEntry> workEntryTable =
            new TableView<>();

    private StoredWorkEntry selectedWorkEntry;

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
        resultArea.setPrefRowCount(11);

        configureTable();
        loadEmployee();
        refreshWorkEntryTable();

        VBox root = new VBox(
                14,
                createTitle(),
                createEmployeeSection(),
                new Separator(),
                createWorkEntrySection(),
                workEntryTable,
                createTableButtons(),
                new Separator(),
                createReportSection(),
                resultArea
        );

        root.setPadding(new Insets(20));

        Scene scene = new Scene(
                root,
                900,
                900
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

        grid.add(new Label("Vorname:"), 0, 0);
        grid.add(firstNameField, 1, 0);

        grid.add(new Label("Nachname:"), 0, 1);
        grid.add(lastNameField, 1, 1);

        grid.add(new Label("Stundenlohn:"), 0, 2);
        grid.add(hourlyWageField, 1, 2);

        grid.add(new Label("Beschäftigung:"), 0, 3);
        grid.add(employmentTypeBox, 1, 3);

        grid.add(studentCheckBox, 1, 4);

        Button saveButton =
                new Button(
                        "Mitarbeiter speichern"
                );

        saveButton.setOnAction(
                event -> saveEmployee()
        );

        grid.add(saveButton, 1, 5);

        return grid;
    }

    private GridPane createWorkEntrySection() {
        GridPane grid = createGrid();

        grid.add(new Label("Datum:"), 0, 0);
        grid.add(workDatePicker, 1, 0);

        grid.add(new Label("Startzeit:"), 0, 1);
        grid.add(startTimeField, 1, 1);

        grid.add(new Label("Endzeit:"), 0, 2);
        grid.add(endTimeField, 1, 2);

        grid.add(new Label("Pause in Minuten:"), 0, 3);
        grid.add(breakMinutesField, 1, 3);

        Button saveButton =
                new Button(
                        "Neue Schicht speichern"
                );

        saveButton.setOnAction(
                event -> saveWorkEntry()
        );

        Button updateButton =
                new Button(
                        "Ausgewählte Schicht ändern"
                );

        updateButton.setOnAction(
                event -> updateSelectedWorkEntry()
        );

        grid.add(saveButton, 1, 4);
        grid.add(updateButton, 2, 4);

        return grid;
    }

    private HBox createTableButtons() {
        Button loadButton =
                new Button(
                        "Monat laden"
                );

        loadButton.setOnAction(
                event -> refreshWorkEntryTable()
        );

        Button deleteButton =
                new Button(
                        "Ausgewählte Schicht löschen"
                );

        deleteButton.setOnAction(
                event -> deleteSelectedWorkEntry()
        );

        Button clearButton =
                new Button(
                        "Auswahl aufheben"
                );

        clearButton.setOnAction(event -> {
            workEntryTable
                    .getSelectionModel()
                    .clearSelection();

            selectedWorkEntry = null;
            resetWorkEntryFields();
        });

        return new HBox(
                10,
                loadButton,
                deleteButton,
                clearButton
        );
    }

    private GridPane createReportSection() {
        GridPane grid = createGrid();

        grid.add(new Label("Monat:"), 0, 0);
        grid.add(reportMonthField, 1, 0);

        Button reportButton =
                new Button(
                        "Monatsabrechnung berechnen"
                );

        reportButton.setOnAction(event -> {
            refreshWorkEntryTable();
            showMonthlyReport();
        });

        grid.add(reportButton, 1, 1);

        Button csvButton =
                new Button("CSV exportieren");

        csvButton.setOnAction(
                event -> exportCsv()
        );

        Button textButton =
                new Button("Abrechnung als TXT speichern");

        textButton.setOnAction(
                event -> exportText()
        );

        grid.add(csvButton, 2, 1);
        grid.add(textButton, 3, 1);

        return grid;
    }

    private void configureTable() {
        TableColumn<StoredWorkEntry, String> dateColumn =
                new TableColumn<>("Datum");

        dateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .date()
                                .toString()
                )
        );

        TableColumn<StoredWorkEntry, String> startColumn =
                new TableColumn<>("Start");

        startColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .startTime()
                                .toString()
                )
        );

        TableColumn<StoredWorkEntry, String> endColumn =
                new TableColumn<>("Ende");

        endColumn.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue()
                                .endTime()
                                .toString()
                )
        );

        TableColumn<StoredWorkEntry, Number> breakColumn =
                new TableColumn<>("Pause");

        breakColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue()
                                .breakMinutes()
                )
        );

        workEntryTable.getColumns().setAll(
                dateColumn,
                startColumn,
                endColumn,
                breakColumn
        );

        workEntryTable.setPrefHeight(220);

        workEntryTable
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            selectedWorkEntry = newValue;

                            if (newValue != null) {
                                fillWorkEntryFields(newValue);
                            }
                        }
                );
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

        BigDecimal hourlyWage =
                new BigDecimal(
                        hourlyWageField
                                .getText()
                                .trim()
                                .replace(",", ".")
                );

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

    private WorkEntry readWorkEntry() {
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

        return new WorkEntry(
                date,
                startTime,
                endTime,
                breakMinutes
        );
    }

    private void saveEmployee() {
        try {
            employeeRepository.save(
                    readEmployee()
            );

            showInformation(
                    "Mitarbeiter wurde gespeichert."
            );
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void saveWorkEntry() {
        try {
            requireEmployee();

            workEntryRepository.save(
                    EMPLOYEE_ID,
                    readWorkEntry()
            );

            resetWorkEntryFields();
            refreshWorkEntryTable();

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

    private void updateSelectedWorkEntry() {
        try {
            if (selectedWorkEntry == null) {
                throw new IllegalStateException(
                        "Zuerst eine Schicht in der Tabelle auswählen."
                );
            }

            WorkEntry changedEntry =
                    readWorkEntry();

            workEntryRepository.update(
                    new StoredWorkEntry(
                            selectedWorkEntry.id(),
                            selectedWorkEntry.employeeId(),
                            changedEntry.getDate(),
                            changedEntry.getStartTime(),
                            changedEntry.getEndTime(),
                            changedEntry.getBreakMinutes()
                    )
            );

            selectedWorkEntry = null;
            resetWorkEntryFields();
            refreshWorkEntryTable();

            showInformation(
                    "Schicht wurde geändert."
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

    private void deleteSelectedWorkEntry() {
        try {
            StoredWorkEntry entry =
                    workEntryTable
                            .getSelectionModel()
                            .getSelectedItem();

            if (entry == null) {
                throw new IllegalStateException(
                        "Zuerst eine Schicht auswählen."
                );
            }

            Alert confirmation =
                    new Alert(
                            Alert.AlertType.CONFIRMATION
                    );

            confirmation.setTitle(
                    "Schicht löschen"
            );

            confirmation.setHeaderText(
                    "Soll die ausgewählte Schicht gelöscht werden?"
            );

            confirmation.setContentText(
                    entry.date()
                            + " | "
                            + entry.startTime()
                            + "–"
                            + entry.endTime()
            );

            Optional<ButtonType> result =
                    confirmation.showAndWait();

            if (result.isEmpty()
                    || result.get()
                    != ButtonType.OK) {
                return;
            }

            workEntryRepository.deleteById(
                    EMPLOYEE_ID,
                    entry.id()
            );

            selectedWorkEntry = null;
            resetWorkEntryFields();
            refreshWorkEntryTable();

            showInformation(
                    "Schicht wurde gelöscht."
            );
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void refreshWorkEntryTable() {
        try {
            YearMonth month =
                    readReportMonth();

            List<StoredWorkEntry> entries =
                    workEntryRepository
                            .findStoredByEmployeeAndMonth(
                                    EMPLOYEE_ID,
                                    month
                            );

            workEntryTable.setItems(
                    FXCollections.observableArrayList(
                            entries
                    )
            );
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void showMonthlyReport() {
        try {
            Employee employee =
                    requireEmployee();

            YearMonth month =
                    readReportMonth();

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

            lastEmployee = employee;
            lastMonthlyReport = monthlyReport;
            lastPayroll = payroll;
            lastStoredEntries = workEntryRepository
                    .findStoredByEmployeeAndMonth(
                            employee.getId(),
                            month
                    );

            lastReportText = createReportText(
                    employee,
                    monthlyReport,
                    payroll
            );

            resultArea.setText(lastReportText);
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void exportCsv() {
        try {
            requireCalculatedReport();

            FileChooser chooser = new FileChooser();
            chooser.setTitle("CSV-Datei speichern");
            chooser.setInitialFileName(
                    "arbeitszeiten-"
                            + lastMonthlyReport.getMonth()
                            + ".csv"
            );

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "CSV-Dateien",
                            "*.csv"
                    )
            );

            File file = chooser.showSaveDialog(
                    workEntryTable.getScene().getWindow()
            );

            if (file == null) {
                return;
            }

            reportExportService.exportCsv(
                    file.toPath(),
                    lastEmployee,
                    lastMonthlyReport.getMonth(),
                    lastStoredEntries,
                    lastMonthlyReport,
                    lastPayroll
            );

            showInformation(
                    "CSV-Datei wurde gespeichert."
            );
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void exportText() {
        try {
            requireCalculatedReport();

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Abrechnung speichern");
            chooser.setInitialFileName(
                    "monatsabrechnung-"
                            + lastMonthlyReport.getMonth()
                            + ".txt"
            );

            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Textdateien",
                            "*.txt"
                    )
            );

            File file = chooser.showSaveDialog(
                    resultArea.getScene().getWindow()
            );

            if (file == null) {
                return;
            }

            reportExportService.exportText(
                    file.toPath(),
                    lastReportText
            );

            showInformation(
                    "Abrechnung wurde gespeichert."
            );
        } catch (Exception exception) {
            showError(exception.getMessage());
        }
    }

    private void requireCalculatedReport() {
        if (lastEmployee == null
                || lastMonthlyReport == null
                || lastPayroll == null
                || lastStoredEntries == null
                || lastReportText == null) {

            throw new IllegalStateException(
                    "Zuerst Monatsabrechnung berechnen."
            );
        }
    }

    private Employee requireEmployee() {
        return employeeRepository
                .findById(EMPLOYEE_ID)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Zuerst Mitarbeiter speichern."
                                )
                );
    }

    private YearMonth readReportMonth() {
        try {
            return YearMonth.parse(
                    reportMonthField
                            .getText()
                            .trim()
            );
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Monat muss beispielsweise 2026-07 sein."
            );
        }
    }

    private void fillWorkEntryFields(
            StoredWorkEntry entry
    ) {
        workDatePicker.setValue(
                entry.date()
        );

        startTimeField.setText(
                entry.startTime().toString()
        );

        endTimeField.setText(
                entry.endTime().toString()
        );

        breakMinutesField.setText(
                Integer.toString(
                        entry.breakMinutes()
                )
        );
    }

    private void resetWorkEntryFields() {
        workDatePicker.setValue(
                LocalDate.now()
        );

        startTimeField.setText("08:00");
        endTimeField.setText("16:00");
        breakMinutesField.setText("30");
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

    private void showInformation(
            String message
    ) {
        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle("Erfolg");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(
            String message
    ) {
        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle("Fehler");
        alert.setHeaderText(
                "Vorgang konnte nicht ausgeführt werden."
        );

        alert.setContentText(
                message == null
                        ? "Unbekannter Fehler."
                        : message
        );

        alert.showAndWait();
    }
}
