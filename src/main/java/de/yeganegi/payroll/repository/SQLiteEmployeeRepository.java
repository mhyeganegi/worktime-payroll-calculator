package de.yeganegi.payroll.repository;

import de.yeganegi.payroll.database.DatabaseManager;
import de.yeganegi.payroll.model.Employee;
import de.yeganegi.payroll.model.EmploymentType;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class SQLiteEmployeeRepository
        implements EmployeeRepository {

    @Override
    public void save(Employee employee) {

        if (employee == null) {
            throw new IllegalArgumentException(
                    "Mitarbeiter darf nicht null sein."
            );
        }

        String sql = """
                INSERT INTO employee (
                    id,
                    first_name,
                    last_name,
                    student,
                    employment_type,
                    hourly_wage
                )
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    first_name = excluded.first_name,
                    last_name = excluded.last_name,
                    student = excluded.student,
                    employment_type = excluded.employment_type,
                    hourly_wage = excluded.hourly_wage
                """;

        try (
                Connection connection =
                        DatabaseManager.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setLong(
                    1,
                    employee.getId()
            );

            statement.setString(
                    2,
                    employee.getFirstName()
            );

            statement.setString(
                    3,
                    employee.getLastName()
            );

            statement.setBoolean(
                    4,
                    employee.isStudent()
            );

            statement.setString(
                    5,
                    employee.getEmploymentType().name()
            );

            statement.setString(
                    6,
                    employee.getHourlyWage()
                            .toPlainString()
            );

            statement.executeUpdate();

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Mitarbeiter konnte nicht gespeichert werden.",
                    exception
            );
        }
    }

    @Override
    public Optional<Employee> findById(
            long employeeId
    ) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Mitarbeiter-ID muss größer als 0 sein."
            );
        }

        String sql = """
                SELECT
                    id,
                    first_name,
                    last_name,
                    student,
                    employment_type,
                    hourly_wage
                FROM employee
                WHERE id = ?
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

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                Employee employee =
                        new Employee(
                                resultSet.getLong("id"),
                                resultSet.getString(
                                        "first_name"
                                ),
                                resultSet.getString(
                                        "last_name"
                                ),
                                resultSet.getBoolean(
                                        "student"
                                ),
                                EmploymentType.valueOf(
                                        resultSet.getString(
                                                "employment_type"
                                        )
                                ),
                                new BigDecimal(
                                        resultSet.getString(
                                                "hourly_wage"
                                        )
                                )
                        );

                return Optional.of(employee);
            }

        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Mitarbeiter konnte nicht geladen werden.",
                    exception
            );
        }
    }
}
