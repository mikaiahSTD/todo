package com.example.demo.service.ticket;

import com.example.demo.endpoint.event.model.SendEmailProfileValidated;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

@Component
public class TicketPdfGenerator {

  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneOffset.UTC);

  public byte[] generate(SendEmailProfileValidated event) throws IOException {
    try (PDDocument document = new PDDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      PDPage page = new PDPage(PDRectangle.A4);
      document.addPage(page);

      try (PDPageContentStream content = new PDPageContentStream(document, page)) {
        float margin = 50;
        float y = page.getMediaBox().getHeight() - margin;
        float width = page.getMediaBox().getWidth() - 2 * margin;

        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
        content.newLineAtOffset(margin, y);
        content.showText("Ticket d'inscription");
        content.endText();
        y -= 40;

        content.setLineWidth(1f);
        content.moveTo(margin, y);
        content.lineTo(margin + width, y);
        content.stroke();
        y -= 30;

        y = writeLine(content, margin, y, "Cours", event.getFileName());
        y =
            writeLine(
                content,
                margin,
                y,
                "Participant",
                (event.getFileName() != null ? event.getFileName() : ""));
        y = writeLine(content, margin, y, "Email", event.getTo());
        if (event.getCourseEndDate() != null) {
          y = writeLine(content, margin, y, "Fin", DATE_FORMAT.format(event.getCourseEndDate()));
        }
        y =
            writeLine(
                content,
                margin,
                y,
                "Référence",
                event.getProfileId() != null ? event.getProfileId().toString() : "");

        y -= 20;
        content.beginText();
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 10);
        content.newLineAtOffset(margin, y);
        content.showText("Présentez ce ticket (ou son QR/référence) à l'entrée du cours.");
        content.endText();
      }

      document.save(out);
      return out.toByteArray();
    }
  }

  private float writeLine(PDPageContentStream content, float x, float y, String label, String value)
      throws IOException {
    content.beginText();
    content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
    content.newLineAtOffset(x, y);
    content.showText(label + " : ");
    content.endText();

    content.beginText();
    content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
    content.newLineAtOffset(x + 90, y);
    content.showText(value != null ? value : "");
    content.endText();

    return y - 22;
  }
}
