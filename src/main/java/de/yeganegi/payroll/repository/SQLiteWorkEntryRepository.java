package de.yeganegi.payroll.repository;

import de.yeganegi.payroll.database.DatabaseManager;
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
        validateEmployeeId(employeeId);

        Objects.requireNonNull(
                month,
                "Monat darf nicht null sein."
        );

        String sql = """
                SELECT
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

        List<WorkEntry> workEntries =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setLong(
                    1,
                    employeeId
            );

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
                    workEntries.add(
                            new WorkEntry(
                                    LocalDate.parse(
                                            resultSet.getString(
                                                    "work_date"
                                            )
                                    ),
                                    LocalTime.parse(
                                            resultSet.getString(
                                                    "start_time"
                                            )
                                    ),
                                    LocalTime.parse(
                                            resultSet.getString(
                                                    "end_time"
                                            )
                                    ),
                                    resultSet.getInt(
                                            "break_minutes"
                                    )
                            )
                    );
                }
            }

            return List.copyOf(workEntries);

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Schichten konnten nicht geladen werden.",
                    exception
            );
        }
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
