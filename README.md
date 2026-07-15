# Worktime Payroll Calculator

Java-Anwendung zur Erfassung und Auswertung von Arbeitszeiten sowie zur späteren Berechnung von Brutto- und Nettoeinkommen.

## Erste Zielgruppe

- Studierende
- Werkstudierende
- Minijobber

Die Architektur wird so aufgebaut, dass später auch Midijob, Teilzeit und Vollzeit unterstützt werden.

## Fachregel für Arbeitstage

- `0 Stunden` = `0 Arbeitstage`
- `mehr als 0 bis einschließlich 4 Stunden` = `0,5 Arbeitstage`
- `mehr als 4 Stunden` = `1 Arbeitstag`

Die tatsächlichen Arbeitsstunden und die bewerteten Arbeitstage werden getrennt behandelt. Der Bruttolohn wird anhand der tatsächlichen Arbeitsstunden berechnet.

## Aktueller Stand

- Maven-Projekt mit Java 21
- Modell für Arbeitstagstypen
- Berechnung von keinem, halbem und vollem Arbeitstag
- JUnit-Tests für Grenzwerte und Fehlerfälle
- Konsolendemonstration

## Starten

```bash
mvn clean test
mvn exec:java -Dexec.mainClass="de.yeganegi.payroll.Main"
```

Alternativ über die IDE die Klasse `Main` starten.

## Geplante Funktionen

1. Arbeitsbeginn, Arbeitsende und Pause
2. Nachtschichten über Mitternacht
3. Beschäftigte und Beschäftigungsarten
4. Stundenlohn und Bruttoeinkommen
5. Studierenden- und Werkstudenten-Auswertungen
6. konfigurierbare Abzüge
7. Speicherung mit SQLite
8. JavaFX-Oberfläche
9. Monatsabrechnung und PDF-Export

> Steuer- und Sozialversicherungsberechnungen werden später nach Berechnungsjahr konfigurierbar implementiert. Ergebnisse sollen als Berechnungshilfe und nicht als rechtsverbindliche Lohnabrechnung gekennzeichnet werden.
