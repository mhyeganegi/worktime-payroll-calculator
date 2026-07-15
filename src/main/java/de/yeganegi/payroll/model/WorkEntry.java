package de.yeganegi.payroll.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class WorkEntry {

    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int breakMinutes;

    public WorkEntry(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            int breakMinutes
    ) {
        this.date = Objects.requireNonNull(
                date,
                "Das Datum darf nicht null sein."
        );

        this.startTime = Objects.requireNonNull(
                startTime,
                "Die Startzeit darf nicht null sein."
        );

        this.endTime = Objects.requireNonNull(
                endTime,
                "Die Endzeit darf nicht null sein."
        );

        if (breakMinutes < 0) {
            throw new IllegalArgumentException(
                    "Die Pause darf nicht negativ sein."
            );
        }

        this.breakMinutes = breakMinutes;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getBreakMinutes() {
        return breakMinutes;
    }
}