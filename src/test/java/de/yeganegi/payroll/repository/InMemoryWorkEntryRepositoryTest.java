package de.yeganegi.payroll.repository;

import de.yeganegi.payroll.model.WorkEntry;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryWorkEntryRepositoryTest {

    private final WorkEntryRepository repository =
            new InMemoryWorkEntryRepository();

    @Test
    void shouldSaveAndFindEntriesForEmployeeAndMonth() {
        repository.save(
                1,
                new WorkEntry(
                        LocalDate.of(2026, 7, 1),
                        LocalTime.of(8, 0),
                        LocalTime.of(12, 0),
                        0
                )
        );

        repository.save(
                1,
                new WorkEntry(
                        LocalDate.of(2026, 8, 1),
                        LocalTime.of(8, 0),
                        LocalTime.of(16, 0),
                        30
                )
        );

        List<WorkEntry> result =
                repository.findByEmployeeAndMonth(
                        1,
                        YearMonth.of(2026, 7)
                );

        assertEquals(1, result.size());
        assertEquals(
                LocalDate.of(2026, 7, 1),
                result.getFirst().getDate()
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoEntriesExist() {
        List<WorkEntry> result =
                repository.findByEmployeeAndMonth(
                        1,
                        YearMonth.of(2026, 7)
                );

        assertEquals(0, result.size());
    }

    @Test
    void shouldRejectInvalidEmployeeIdWhenSaving() {
        WorkEntry entry = new WorkEntry(
                LocalDate.of(2026, 7, 1),
                LocalTime.of(8, 0),
                LocalTime.of(12, 0),
                0
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(0, entry)
        );
    }

    @Test
    void shouldRejectInvalidEmployeeIdWhenSearching() {
        assertThrows(
                IllegalArgumentException.class,
                () -> repository.findByEmployeeAndMonth(
                        0,
                        YearMonth.of(2026, 7)
                )
        );
    }

    @Test
    void shouldRejectNullMonth() {
        assertThrows(
                NullPointerException.class,
                () -> repository.findByEmployeeAndMonth(
                        1,
                        null
                )
        );
    }
}
