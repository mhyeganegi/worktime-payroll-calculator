package de.yeganegi.payroll.model;

import java.time.LocalDate;
import java.time.LocalTime;

public record StoredWorkEntry(
        long id,
        long employeeId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        int breakMinutes
) {

    public StoredWorkEntry {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Schicht-ID muss größer als 0 sein."
            );
        }

        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Mitarbeiter-ID muss größer als 0 sein."
            );
        }

        if (date == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException(
                    "Datum und Uhrzeiten dürfen nicht null sein."
            );
        }

        if (breakMinutes < 0) {
            throw new IllegalArgumentException(
                    "Pause darf nicht negativ sein."
            );
        }
    }

    public WorkEntry toWorkEntry() {
        return new WorkEntry(
                date,
                startTime,
                endTime,
                breakMinutes
        );
    }
}
