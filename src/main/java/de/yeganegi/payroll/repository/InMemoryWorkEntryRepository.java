package de.yeganegi.payroll.repository;

import de.yeganegi.payroll.model.WorkEntry;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class InMemoryWorkEntryRepository
        implements WorkEntryRepository {

    private final Map<Long, List<WorkEntry>> entriesByEmployee =
            new HashMap<>();

    @Override
    public void save(long employeeId, WorkEntry workEntry) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Mitarbeiter-ID muss größer als 0 sein."
            );
        }

        Objects.requireNonNull(
                workEntry,
                "Arbeitseintrag darf nicht null sein."
        );

        entriesByEmployee
                .computeIfAbsent(
                        employeeId,
                        ignored -> new ArrayList<>()
                )
                .add(workEntry);
    }

    @Override
    public List<WorkEntry> findByEmployeeAndMonth(
            long employeeId,
            YearMonth month
    ) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Mitarbeiter-ID muss größer als 0 sein."
            );
        }

        Objects.requireNonNull(
                month,
                "Monat darf nicht null sein."
        );

        return entriesByEmployee
                .getOrDefault(employeeId, List.of())
                .stream()
                .filter(entry ->
                        YearMonth.from(entry.getDate()).equals(month)
                )
                .toList();
    }
}
