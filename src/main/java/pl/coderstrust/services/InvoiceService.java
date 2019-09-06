package pl.coderstrust.services;

import java.io.IOException;
import java.util.Collection;
import javax.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.coderstrust.database.Database;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.EmailService.Operation;

@Service
@Slf4j
public class InvoiceService {

  private Database database;
  private EmailService emailService;

  public InvoiceService(Database database, EmailService emailService) {
    this.database = database;
    this.emailService = emailService;
  }

  public void saveInvoice(Invoice invoice) throws IOException {
    database.saveInvoice(invoice);
    emailService.addToReport(Operation.INVOICE_ADDED, invoice.getInvoiceNumber());
    try {
      emailService.sendInvoiceCreatedMail(invoice);
    } catch (MessagingException e) {
      log.error("An error occurred while sending the email", e);
    }
  }

  public Collection<Invoice> getInvoices() throws IOException {
    return database.getInvoices();
  }

  public Invoice getInvoiceById(Long id) throws IOException {
    return database.getInvoiceById(id);
  }

  public boolean updateInvoice(Long id, Invoice invoice) throws IOException {
    boolean result = database.updateInvoice(id, invoice);
    if (result) {
      emailService.addToReport(Operation.INVOICE_MODIFIED, invoice.getInvoiceNumber());
    }
    return result;
  }

  public boolean removeInvoiceById(Long id) throws IOException {
    boolean result = database.removeInvoiceById(id);
    if (result) {
      emailService.addToReport(Operation.INVOICE_REMOVED, id.toString());
    }
    return result;
  }
}
