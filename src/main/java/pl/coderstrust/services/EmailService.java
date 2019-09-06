package pl.coderstrust.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

@Service
@PropertySource("classpath:email.properties")
@Slf4j
public class EmailService {

  public enum Operation {
    INVOICE_ADDED,
    INVOICE_MODIFIED,
    INVOICE_REMOVED
  }

  private JavaMailSender sender;
  private Map<Operation, List<String>> report;
  private String emailAddress;
  private boolean reportEnabled;

  EmailService(JavaMailSender sender, @Value("${email.address}") String emailAddress,
      @Value("${email.report.enabled}") boolean reportEnabled) {
    this.sender = sender;
    this.emailAddress = emailAddress;
    this.reportEnabled = reportEnabled;
    report = new HashMap<>();
    Arrays.stream(Operation.values()).forEach(operation -> report.put(operation, new ArrayList<>()));
  }

  Map<Operation, List<String>> getReport() {
    return report;
  }

  void sendInvoiceCreatedMail(Invoice invoice) throws MessagingException {
    sendMail("New Invoice has been added.", getEmailMessage(invoice));
  }

  private void sendMail(String subject, String messageText) throws MessagingException {
    MimeMessage message = sender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);
    helper.setTo(emailAddress);
    helper.setSubject(subject);
    helper.setText(messageText);
    HttpHeaders responseHeaders = new HttpHeaders();
    log.info("An email is being sent");
    sender.send(message);
    log.info("An email has been sent");
  }

  private String getEmailMessage(Invoice invoice) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("\n\nInvoice number: ");
    stringBuilder.append(invoice.getInvoiceNumber());
    stringBuilder.append("\n\n");
    stringBuilder.append("Buyer:");
    stringBuilder.append("\n");
    stringBuilder.append(invoice.getBuyer().getName());
    stringBuilder.append("\n");
    stringBuilder.append(invoice.getBuyer().getAddress().getStreetAddress());
    stringBuilder.append("\n");
    stringBuilder.append(invoice.getBuyer().getAddress().getPostalCode());
    stringBuilder.append(" ");
    stringBuilder.append(invoice.getBuyer().getAddress().getCity());
    stringBuilder.append("\nNIP: ");
    stringBuilder.append(invoice.getBuyer().getTaxIdentificationNumber());
    stringBuilder.append("\n\nInvoice entries:\n");
    for (InvoiceEntry entry : invoice.getInvoiceEntries()) {
      stringBuilder.append("name: ");
      stringBuilder.append(entry.getDescription());
      stringBuilder.append(", quantity: ");
      stringBuilder.append(entry.getQuantity());
      stringBuilder.append(", value: ");
      stringBuilder.append(entry.getValue());
      stringBuilder.append(", tax value: ");
      stringBuilder.append(entry.getVatValue());
      stringBuilder.append(", vat rate: ");
      stringBuilder.append(entry.getVatRate());
      stringBuilder.append("\n");
    }
    return stringBuilder.toString();
  }

  void addToReport(Operation operation, String number) {
    report.get(operation).add(number);
  }

  private void sendReportCreatedMail() throws MessagingException {
    sendMail("Daily report of invoices.", getReportMessage());
  }

  private String getReportMessage() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("\nReport of invoices - date: ");
    stringBuilder.append(LocalDate.now().minusDays(1L));
    stringBuilder.append("\n\nAdded invoices:\n");
    if (report.get(Operation.INVOICE_ADDED).size() == 0) {
      stringBuilder.append("No invoices.\n");
    } else {
      for (String number : report.get(Operation.INVOICE_ADDED)) {
        stringBuilder.append(number);
        stringBuilder.append("\n");
      }
    }
    stringBuilder.append("\nModified invoices:\n");
    if (report.get(Operation.INVOICE_MODIFIED).size() == 0) {
      stringBuilder.append("No invoices.\n");
    } else {
      for (String number : report.get(Operation.INVOICE_MODIFIED)) {
        stringBuilder.append(number);
        stringBuilder.append("\n");
      }
    }
    stringBuilder.append("\nRemoved invoices:\n");
    if (report.get(Operation.INVOICE_REMOVED).size() == 0) {
      stringBuilder.append("No invoices.\n");
    } else {
      for (String number : report.get(Operation.INVOICE_REMOVED)) {
        stringBuilder.append(number);
        stringBuilder.append("\n");
      }
    }
    return stringBuilder.toString();
  }

  @Scheduled(cron = "0 0 0 * * *")
  void sendReport() {
    if (reportEnabled) {
      try {
        sendReportCreatedMail();
        report.get(Operation.INVOICE_ADDED).clear();
        report.get(Operation.INVOICE_MODIFIED).clear();
        report.get(Operation.INVOICE_REMOVED).clear();
      } catch (MessagingException e) {
        log.error("An error occurred while sending the email", e);
      }
    }
  }
}
