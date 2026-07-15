package de.yeganegi.payroll.repository;

import de.yeganegi.payroll.model.WorkEntry;

import java.time.YearMonth;
import java.util.List;

public interface WorkEntryRepository {

    void save(long employeeId, WorkEntry workEntry);

    List<WorkEntry> findByEmployeeAndMonth(
            long employeeId,
            YearMonth month
    );
}
