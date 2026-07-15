package de.yeganegi.payroll.export;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PdfReportExportService {

    private static final float MARGIN = 50;
    private static final float TITLE_FONT_SIZE = 18;
    private static final float TEXT_FONT_SIZE = 11;
    private static final float LINE_HEIGHT = 16;

    private final PDType1Font titleFont =
            new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA_BOLD
            );

    private final PDType1Font textFont =
            new PDType1Font(
                    Standard14Fonts.FontName.HELVETICA
            );

    public void export(
            Path target,
            String reportText
    ) {
        Objects.requireNonNull(
                target,
                "Zieldatei darf nicht null sein."
        );

        Objects.requireNonNull(
                reportText,
                "Abrechnungstext darf nicht null sein."
        );

        createParentDirectory(target);

        try (PDDocument document = new PDDocument()) {
            List<String> lines =
                    prepareLines(reportText);

            writePages(document, lines);

            document.save(target.toFile());
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "PDF-Datei konnte nicht erstellt werden.",
                    exception
            );
        }
    }

    private void writePages(
            PDDocument document,
            List<String> lines
    ) throws IOException {
        PDPage page =
                createPage(document);

        PDPageContentStream content =
                new PDPageContentStream(
                        document,
                        page
                );

        float y =
                page.getMediaBox().getHeight()
                        - MARGIN;

        y = writeTitle(content, y);

        for (String line : lines) {
            if (y < MARGIN + LINE_HEIGHT) {
                content.close();

                page = createPage(document);

                content =
                        new PDPageContentStream(
                                document,
                                page
                        );

                y =
                        page.getMediaBox().getHeight()
                                - MARGIN;
            }

            writeLine(
                    content,
                    line,
                    y
            );

            y -= LINE_HEIGHT;
        }

        content.close();
    }

    private PDPage createPage(
            PDDocument document
    ) {
        PDPage page =
                new PDPage(PDRectangle.A4);

        document.addPage(page);

        return page;
    }

    private float writeTitle(
            PDPageContentStream content,
            float y
    ) throws IOException {
        content.beginText();
        content.setFont(
                titleFont,
                TITLE_FONT_SIZE
        );
        content.newLineAtOffset(
                MARGIN,
                y
        );
        content.showText(
                "Worktime Payroll Calculator"
        );
        content.endText();

        return y - 32;
    }

    private void writeLine(
            PDPageContentStream content,
            String line,
            float y
    ) throws IOException {
        content.beginText();
        content.setFont(
                textFont,
                TEXT_FONT_SIZE
        );
        content.newLineAtOffset(
                MARGIN,
                y
        );
        content.showText(
                sanitize(line)
        );
        content.endText();
    }

    private List<String> prepareLines(
            String reportText
    ) throws IOException {
        List<String> result =
                new ArrayList<>();

        for (String originalLine
                : reportText.split("\\R")) {

            result.addAll(
                    wrapLine(
                            sanitize(originalLine),
                            90
                    )
            );
        }

        return result;
    }

    private List<String> wrapLine(
            String line,
            int maximumLength
    ) {
        List<String> result =
                new ArrayList<>();

        if (line.isBlank()) {
            result.add("");
            return result;
        }

        String remaining = line;

        while (remaining.length()
                > maximumLength) {

            int splitPosition =
                    remaining.lastIndexOf(
                            ' ',
                            maximumLength
                    );

            if (splitPosition <= 0) {
                splitPosition =
                        maximumLength;
            }

            result.add(
                    remaining
                            .substring(
                                    0,
                                    splitPosition
                            )
                            .trim()
            );

            remaining =
                    remaining
                            .substring(
                                    splitPosition
                            )
                            .trim();
        }

        result.add(remaining);

        return result;
    }

    private String sanitize(String value) {
        return value
                .replace("€", "EUR")
                .replace("–", "-")
                .replace("—", "-")
                .replace("“", "\"")
                .replace("”", "\"");
    }

    private void createParentDirectory(
            Path target
    ) {
        Path parent = target.getParent();

        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Exportordner konnte nicht erstellt werden.",
                    exception
            );
        }
    }
}
