package de.yeganegi.payroll.repository;

import de.yeganegi.payroll.database.DatabaseManager;
import de.yeganegi.payroll.model.StoredWorkEntry;
import de.yeganegi.payroll.model.WorkEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SQLiteWorkEntryRepository
        implements WorkEntryRepository {

    @Override
    public void save(
            long employeeId,
            WorkEntry workEntry
    ) {
        validateEmployeeId(employeeId);

        Objects.requireNonNull(
                workEntry,
                "Arbeitseintrag darf nicht null sein."
        );

        String sql = """
                INSERT INTO work_entry (
                    employee_id,
                    work_date,
                    start_time,
                    end_time,
                    break_minutes
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            setWorkEntryValues(
                    statement,
                    employeeId,
                    workEntry
            );

            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Schicht konnte nicht gespeichert werden.",
                    exception
            );
        }
    }

    @Override
    public List<WorkEntry> findByEmployeeAndMonth(
            long employeeId,
            YearMonth month
    ) {
        return findStoredByEmployeeAndMonth(
                employeeId,
                month
        )
                .stream()
                .map(StoredWorkEntry::toWorkEntry)
                .toList();
    }

    public List<StoredWorkEntry> findStoredByEmployeeAndMonth(
            long employeeId,
            YearMonth month
    ) {
        validateEmployeeId(employeeId);

        Objects.requireNonNull(
                month,
                "Monat darf nicht null sein."
        );

        String sql = """
                SELECT
                    id,
                    employee_id,
                    work_date,
                    start_time,
                    end_time,
                    break_minutes
                FROM work_entry
                WHERE employee_id = ?
                  AND work_date >= ?
                  AND work_date < ?
                ORDER BY work_date, start_time
                """;

        List<StoredWorkEntry> entries =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setLong(1, employeeId);
            statement.setString(
                    2,
                    month.atDay(1).toString()
            );
            statement.setString(
                    3,
                    month.plusMonths(1)
                            .atDay(1)
                            .toString()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    entries.add(
                            mapStoredWorkEntry(resultSet)
                    );
                }
            }

            return List.copyOf(entries);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Schichten konnten nicht geladen werden.",
                    exception
            );
        }
    }

    public void update(
            StoredWorkEntry storedWorkEntry
    ) {
        Objects.requireNonNull(
                storedWorkEntry,
                "Gespeicherte Schicht darf nicht null sein."
        );

        String sql = """
                UPDATE work_entry
                SET
                    work_date = ?,
                    start_time = ?,
                    end_time = ?,
                    break_minutes = ?
                WHERE id = ?
                  AND employee_id = ?
                """;

        try (
                Connection connection =
                        DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    storedWorkEntry.date().toString()
            );
            statement.setString(
                    2,
                    storedWorkEntry.startTime().toString()
            );
            statement.setString(
                    3,
                    storedWorkEntry.endTime().toString()
            );
            statement.setInt(
                    4,
                    storedWorkEntry.breakMinutes()
            );
            statement.setLong(
                    5,
                    storedWorkEntry.id()
            );
            statement.setLong(
                    6,
                    storedWorkEntry.employeeId()
            );

            int updatedRows =
                    statement.executeUpdate();

            if (updatedRows == 0) {
                throw new IllegalArgumentException(
                        "Schicht wurde nicht gefunden."
                );
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Schicht konnte nicht aktualisiert werden.",
                    exception
            );
        }
    }

    public void deleteById(
            long employeeId,
            long workEntryId
    ) {
        validateEmployeeId(employeeId);

        if (workEntryId <= 0) {
            throw new IllegalArgumentException(
                    "Schicht-ID muss größer als 0 sein."
            );
        }

        String sql = """
                DELETE FROM work_entry
                WHERE id = ?
                  AND employee_id = ?
                """;

        try (
                Connection connection =
                        DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setLong(1, workEntryId);
            statement.setLong(2, employeeId);

            int deletedRows =
                    statement.executeUpdate();

            if (deletedRows == 0) {
                throw new IllegalArgumentException(
                        "Schicht wurde nicht gefunden."
                );
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Schicht konnte nicht gelöscht werden.",
                    exception
            );
        }
    }

    private StoredWorkEntry mapStoredWorkEntry(
            ResultSet resultSet
    ) throws SQLException {
        return new StoredWorkEntry(
                resultSet.getLong("id"),
                resultSet.getLong("employee_id"),
                LocalDate.parse(
                        resultSet.getString("work_date")
                ),
                LocalTime.parse(
                        resultSet.getString("start_time")
                ),
                LocalTime.parse(
                        resultSet.getString("end_time")
                ),
                resultSet.getInt("break_minutes")
        );
    }

    private void setWorkEntryValues(
            PreparedStatement statement,
            long employeeId,
            WorkEntry workEntry
    ) throws SQLException {
        statement.setLong(
                1,
                employeeId
        );
        statement.setString(
                2,
                workEntry.getDate().toString()
        );
        statement.setString(
                3,
                workEntry.getStartTime().toString()
        );
        statement.setString(
                4,
                workEntry.getEndTime().toString()
        );
        statement.setInt(
                5,
                workEntry.getBreakMinutes()
        );
    }

    private void validateEmployeeId(
            long employeeId
    ) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Mitarbeiter-ID muss größer als 0 sein."
            );
        }
    }
}
