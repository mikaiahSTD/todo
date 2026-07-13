package com.example.demo.service.event;

import static java.io.File.createTempFile;

import com.example.demo.endpoint.event.model.SendEmailProfileValidated;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import com.example.demo.service.ticket.TicketPdfGenerator;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SendEmailSubscriptionValidatedService implements Consumer<SendEmailProfileValidated> {
  private static final String TICKETS_PREFIX = "tickets/";

  private final Mailer mailer;
  private final TicketPdfGenerator ticketPdfGenerator;
  private final BucketComponent bucketComponent;

  @SneakyThrows
  @Override
  public void accept(SendEmailProfileValidated requested) {
    byte[] pdfBytes = ticketPdfGenerator.generate(requested);

    File tempPdf = createTempFile(requested.getSubscriptionId().toString(), ".pdf");
    try (FileOutputStream fos = new FileOutputStream(tempPdf)) {
      fos.write(pdfBytes);
    }

    String bucketKey = TICKETS_PREFIX + requested.getSubscriptionId() + ".pdf";
    bucketComponent.upload(tempPdf, bucketKey);

    String presignedUrl = bucketComponent.presign(bucketKey, Duration.ofHours(24)).toString();

    tempPdf.delete();
    InternetAddress recipientAddress = new InternetAddress(requested.getTo());
    String greeting =
        (requested.getFirstName() != null && !requested.getFirstName().isBlank())
            ? requested.getFirstName()
            : "Cher participant";
    String subject = "Votre ticket — " + requested.getCourseTitle();
    mailer.accept(
        new Email(
            recipientAddress,
            List.of(),
            List.of(),
            subject,
            "Bonjour "
                + greeting
                + ",\n\n"
                + "Votre inscription au cours \""
                + requested.getCourseTitle()
                + "\" a bien été enregistrée.\n\n"
                + "Cliquez sur le lien ci-dessous pour télécharger votre ticket :\n"
                + presignedUrl
                + "\n\n"
                + "Ce lien est valable 24 heures.\n\n"
                + "À bientôt !",
            List.of()));
  }
}
