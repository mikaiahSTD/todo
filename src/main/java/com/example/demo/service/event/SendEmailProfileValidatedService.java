package com.example.demo.service.event;

import com.example.demo.endpoint.event.model.SendEmailProfileValidated;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.mail.Email;
import com.example.demo.mail.Mailer;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SendEmailProfileValidatedService implements Consumer<SendEmailProfileValidated> {

  private static final String PROFILES_PREFIX = "profiles/";

  private final Mailer mailer;
  private final BucketComponent bucketComponent;

  @SneakyThrows
  @Override
  public void accept(SendEmailProfileValidated requested) {
    InternetAddress recipientAddress = new InternetAddress(requested.getTo());

    File tempImage = File.createTempFile(requested.getProfileId().toString(), ".png");

    String bucketKey = PROFILES_PREFIX + requested.getProfileId() + ".png";

    bucketComponent.upload(tempImage, bucketKey);

    String presignedUrl = bucketComponent.presign(bucketKey, Duration.ofHours(24)).toString();

    tempImage.delete();

    String subject = "Votre profil — " + requested.getFileName();

    mailer.accept(
        new Email(
            recipientAddress,
            List.of(),
            List.of(),
            subject,
            """
            Bonjour %s,

            Votre inscription au cours "%s" a bien été enregistrée.

            Vous pouvez télécharger votre image ici :
            %s

            Ce lien est valable 24 heures.

            À bientôt !
            """
                .formatted("Bonjour", requested.getFileName(), presignedUrl),
            List.of()));
  }
}
