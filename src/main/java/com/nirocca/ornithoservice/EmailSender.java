package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.model.Sighting;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);

    public void send(List<Sighting> lastSightings) throws IOException {
        Properties sendgritProperties = new Properties();
        sendgritProperties.load(EmailSender.class.getResourceAsStream("/sendgrid.properties"));

        Email to = new Email(sendgritProperties.getProperty("receiver"));
        Email from = new Email(sendgritProperties.getProperty("sender"));
        String subject = "Ornitho Alert";
        Content content = new Content("text/html", createContent(lastSightings));
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sendgrid = new SendGrid(sendgritProperties.getProperty("key"));

        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendgrid.api(request);

        if (response.getStatusCode() != 202) {
            LOGGER.error("An error occurred: {}", response.getStatusCode());
            return;
        }
        LOGGER.info("Email sent.");
    }

    private String createContent(List<Sighting> lastSightings) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append("<html><body><h2>New Sightings:</h2><table><thead><tr><th>Name</th><th>Datum</th><th>Ort</th><th>URL</th><th>Map</th></tr></thead><tbody>");
        for (Sighting s : lastSightings) {
            SightingModel model = new SightingModel(s, CoordinatesExporter.getCoordinates(s.getUrl()), SightingModel.HOME);
            //SightingModel model = new SightingModel(s, CoordinatesExporter.getCoordinates(s.getUrl()), SightingsController.VACATION_SETTINGS.location);

            result.append(String.format("  <tr>"
                + "    <td>%s</td>"
                + "    <td>%s</td>"
                + "    <td>%s</td>"
                + "    <td><a href=\"%s\" target=\"_blank\">url</a></td>"
                + "    <td><a href=\"%s\" target=\"_blank\">map</a></td>"
                + "  </tr>", model.getGermanNamePlural(), model.getDate(), model.getLocation(), model.getUrl(), model.getNavigateUrl()));
        }
        result.append("</tbody></table></body></html>");
        return result.toString();
    }

    public static void main(String[] args) throws IOException { //for testing
        new EmailSender().send(new SightingsCalculator().getLastSightings());
    }
}
