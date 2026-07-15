package de.yeganegi.payroll.database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private static final String DATABASE_DIRECTORY = "data";
    private static final String DATABASE_URL =
            "jdbc:sqlite:data/payroll.db";

    private DatabaseManager() {
    }

    public static Connection getConnection()
            throws SQLException {
        createDatabaseDirectory();
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static void initializeDatabase() {
        String employeeTable = """
                CREATE TABLE IF NOT EXISTS employee (
                    id INTEGER PRIMARY KEY,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    student INTEGER NOT NULL,
                    employment_type TEXT NOT NULL,
                    hourly_wage TEXT NOT NULL
                )
                """;

        String workEntryTable = """
                CREATE TABLE IF NOT EXISTS work_entry (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    employee_id INTEGER NOT NULL,
                    work_date TEXT NOT NULL,
                    start_time TEXT NOT NULL,
                    end_time TEXT NOT NULL,
                    break_minutes INTEGER NOT NULL,
                    FOREIGN KEY (employee_id)
                        REFERENCES employee(id)
                        ON DELETE CASCADE
                )
                """;

        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute(employeeTable);
            statement.execute(workEntryTable);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Datenbank konnte nicht initialisiert werden.",
                    exception
            );
        }
    }

    private static void createDatabaseDirectory() {
        try {
            Files.createDirectories(
                    Path.of(DATABASE_DIRECTORY)
            );
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Datenbankordner konnte nicht erstellt werden.",
                    exception
            );
        }
    }
}
