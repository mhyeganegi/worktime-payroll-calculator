package de.yeganegi.payroll;

import de.yeganegi.payroll.ui.PayrollDesktopApplication;
import javafx.application.Application;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Application.launch(
                PayrollDesktopApplication.class,
                args
        );
    }
}
