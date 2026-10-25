# Schichtbuch – Arbeitszeit und Monatsübersicht

Eine Java-Desktop-App für Studierende und Werkstudierende, die ihre Schichten erfassen und Arbeitszeit sowie geschätzten Bruttolohn pro Monat nachvollziehen möchten.

Die App hilft beim persönlichen Überblick. Sie ersetzt keine Lohnabrechnung und berechnet keine rechtsverbindlichen Steuer- oder Sozialversicherungsbeträge.

## Was die App aktuell kann

- Beschäftigtenprofil mit Beschäftigungsart und Stundenlohn speichern
- Schichten mit Datum, Beginn, Ende und Pause erfassen, bearbeiten und löschen
- Arbeitszeiten über Mitternacht berechnen
- Monatsübersicht mit Stunden, Arbeitstagen und geschätztem Brutto anzeigen
- Berichte als PDF, CSV oder Textdatei exportieren
- Beschäftigten- und Schichtdaten lokal in einer SQLite-Datenbank speichern

Arbeitstage werden für die Übersicht getrennt von den bezahlten Stunden bewertet:

- 0 Stunden = 0 Arbeitstage
- mehr als 0 bis einschließlich 4 Stunden = 0,5 Arbeitstage
- mehr als 4 Stunden = 1 Arbeitstag

Der Bruttolohn wird aus den erfassten Arbeitsstunden und dem Stundenlohn berechnet.

## Voraussetzungen

- Java 21
- Maven 3.9 oder neuer

## Starten

Repository klonen und im Projektordner ausführen:

```bash
mvn clean test
mvn javafx:run
```

In einer IDE kann alternativ die Klasse `de.yeganegi.payroll.Main` gestartet werden.

## Daten und Exporte

Die App legt ihre SQLite-Datenbank derzeit unter `data/payroll.db` relativ zum Startordner an. Exporte werden über die Oberfläche erstellt. Sichere die Datenbankdatei regelmäßig selbst; eine automatische Sicherung und Wiederherstellung ist noch nicht Teil der App.

## Projektstruktur

- `model`: Beschäftigte, Schichten, Lohn- und Monatsübersichtsdaten
- `calculation`: Arbeitszeit-, Brutto- und Abzugsberechnungen
- `repository`: Lesen und Speichern von Beschäftigten und Schichten
- `database`: SQLite-Verbindung und Tabellen
- `export`: PDF-, CSV- und Textberichte
- `ui`: JavaFX-Oberfläche
- `src/test`: Tests für Berechnungen und ausgewählte Datenzugriffe

## Bekannte Grenzen

- Das Profil ist aktuell auf einen Beschäftigten ausgelegt.
- Abzüge und Nettowerte sind unverbindliche Schätzungen.
- Die App ist noch nicht als Installationspaket für macOS oder Windows vorbereitet.
- Es gibt noch keine automatische Datenbanksicherung.

