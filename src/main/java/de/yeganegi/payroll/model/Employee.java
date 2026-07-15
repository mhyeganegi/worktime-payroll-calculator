package de.yeganegi.payroll.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Employee {

    private final long id;
    private final String firstName;
    private final String lastName;
    private final boolean student;
    private final EmploymentType employmentType;
    private final BigDecimal hourlyWage;

    public Employee(
            long id,
            String firstName,
            String lastName,
            boolean student,
            EmploymentType employmentType,
            BigDecimal hourlyWage
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "Die ID muss größer als 0 sein."
            );
        }

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException(
                    "Der Vorname darf nicht leer sein."
            );
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException(
                    "Der Nachname darf nicht leer sein."
            );
        }

        if (hourlyWage == null || hourlyWage.signum() < 0) {
            throw new IllegalArgumentException(
                    "Der Stundenlohn darf nicht negativ sein."
            );
        }

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.student = student;
        this.employmentType = Objects.requireNonNull(
                employmentType,
                "Die Beschäftigungsart darf nicht null sein."
        );
        this.hourlyWage = hourlyWage;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isStudent() {
        return student;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public BigDecimal getHourlyWage() {
        return hourlyWage;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}