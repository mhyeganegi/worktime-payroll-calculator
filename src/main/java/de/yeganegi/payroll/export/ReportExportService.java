package de.yeganegi.payroll.export;

import de.yeganegi.payroll.model.Deduction;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.MonthlyReport;
import de.yeganegi.payroll.model.Payroll;
import de.yeganegi.payroll.model.StoredWorkEntry;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;

public class ReportExportService {

    public void exportCsv(
            Path target,
            Employee employee,
            YearMonth month,
            List<StoredWorkEntry> entries,
            MonthlyReport report,
            Payroll payroll
    ) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(employee);
        Objects.requireNonNull(month);
        Objects.requireNonNull(entries);
        Objects.requireNonNull(report);
        Objects.requireNonNull(payroll);

        StringBuilder csv = new StringBuilder();

        csv.append(
                "Mitarbeiter;Monat;Datum;Start;Ende;Pause Minuten\n"
        );

        for (StoredWorkEntry entry : entries) {
            csv.append(escape(employee.getFullName()))
                    .append(";")
                    .append(month)
                    .append(";")
                    .append(entry.date())
                    .append(";")
                    .append(entry.startTime())
                    .append(";")
                    .append(entry.endTime())
                    .append(";")
                    .append(entry.breakMinutes())
                    .append("\n");
        }

        csv.append("\n");
        csv.append("Zusammenfassung;Wert\n");
        csv.append("Schichten;")
                .append(report.getWorkEntryCount())
                .append("\n");

        csv.append("Arbeitsstunden;")
                .append(report.getTotalWorkingHours())
                .append("\n");

        csv.append("Halbe Tage;")
                .append(report.getHalfDays())
                .append("\n");

        csv.append("Volle Tage;")
                .append(report.getFullDays())
                .append("\n");

        csv.append("Arbeitstage;")
                .append(report.getTotalWorkdays())
                .append("\n");

        csv.append("Brutto;")
                .append(formatMoney(payroll.getGrossIncome()))
                .append("\n");

        for (Deduction deduction : payroll.getDeductions()) {
            csv.append(deduction.getType())
                    .append(";")
                    .append(formatMoney(deduction.getAmount()))
                    .append("\n");
        }

        csv.append("Gesamtabzüge;")
                .append(formatMoney(payroll.getTotalDeductions()))
                .append("\n");

        csv.append("Netto;")
                .append(formatMoney(payroll.getNetIncome()))
                .append("\n");

        writeFile(target, csv.toString());
    }

    public void exportText(
            Path target,
            String reportText
    ) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(
                reportText,
                "Abrechnungstext darf nicht null sein."
        );

        writeFile(target, reportText);
    }

    private String escape(String value) {
        if (value.contains(";")
                || value.contains("\"")
                || value.contains("\n")) {

            return "\""
                    + value.replace("\"", "\"\"")
                    + "\"";
        }

        return value;
    }

    private String formatMoney(BigDecimal value) {
        return value
                .setScale(2)
                .toPlainString();
    }

    private void writeFile(
            Path target,
            String content
    ) {
        try {
            Path parent = target.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                    target,
                    content,
                    StandardCharsets.UTF_8
            );
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Datei konnte nicht exportiert werden.",
                    exception
            );
        }
    }
}
