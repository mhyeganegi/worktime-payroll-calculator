package de.yeganegi.payroll.repository;

import de.yeganegi.payroll.database.DatabaseManager;
import de.yeganegi.payroll.model.PayrollProfile;
import de.yeganegi.payroll.model.TaxClass;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class PayrollProfileRepository {

    public PayrollProfileRepository() {
        initializeTable();
    }

    public void save(PayrollProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException(
                    "Abrechnungsprofil darf nicht null sein."
            );
        }

        String sql = """
                INSERT INTO payroll_profile (
                    employee_id,
                    tax_class,
                    health_insurance_rate,
                    church_tax_enabled
                )
                VALUES (?, ?, ?, ?)
                ON CONFLICT(employee_id) DO UPDATE SET
                    tax_class = excluded.tax_class,
                    health_insurance_rate =
                        excluded.health_insurance_rate,
                    church_tax_enabled =
                        excluded.church_tax_enabled
                """;

        try (
                Connection connection =
                        DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setLong(
                    1,
                    profile.employeeId()
            );

            statement.setString(
                    2,
                    profile.taxClass().name()
            );

            statement.setString(
                    3,
                    profile.healthInsuranceRate()
                            .toPlainString()
            );

            statement.setBoolean(
                    4,
                    profile.churchTaxEnabled()
            );

            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Abrechnungsprofil konnte nicht gespeichert werden.",
                    exception
            );
        }
    }

    public Optional<PayrollProfile> findByEmployeeId(
            long employeeId
    ) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Mitarbeiter-ID muss größer als 0 sein."
            );
        }

        String sql = """
                SELECT
                    employee_id,
                    tax_class,
                    health_insurance_rate,
                    church_tax_enabled
                FROM payroll_profile
                WHERE employee_id = ?
                """;

        try (
                Connection connection =
                        DatabaseManager.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setLong(1, employeeId);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(
                        new PayrollProfile(
                                resultSet.getLong(
                                        "employee_id"
                                ),
                                TaxClass.valueOf(
                                        resultSet.getString(
                                                "tax_class"
                                        )
                                ),
                                new BigDecimal(
                                        resultSet.getString(
                                                "health_insurance_rate"
                                        )
                                ),
                                resultSet.getBoolean(
                                        "church_tax_enabled"
                                )
                        )
                );
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Abrechnungsprofil konnte nicht geladen werden.",
                    exception
            );
        }
    }

    private void initializeTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS payroll_profile (
                    employee_id INTEGER PRIMARY KEY,
                    tax_class TEXT NOT NULL,
                    health_insurance_rate TEXT NOT NULL,
                    church_tax_enabled INTEGER NOT NULL,
                    FOREIGN KEY (employee_id)
                        REFERENCES employee(id)
                        ON DELETE CASCADE
                )
                """;

        try (
                Connection connection =
                        DatabaseManager.getConnection();
                Statement statement =
                        connection.createStatement()
        ) {
            statement.execute(sql);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Tabelle für Abrechnungsprofile konnte nicht erstellt werden.",
                    exception
            );
        }
    }
}
