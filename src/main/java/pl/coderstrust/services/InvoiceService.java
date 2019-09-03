package pl.coderstrust.services;

import java.io.IOException;
import java.util.Collection;
import org.springframework.stereotype.Service;
import pl.coderstrust.database.Database;
import pl.coderstrust.model.Invoice;

@Service
public class InvoiceService {

  private Database database;

  public InvoiceService(Database database) {
    this.database = database;
  }

  public void saveInvoice(Invoice invoice) throws IOException {
    database.saveInvoice(invoice);
  }

  public Collection<Invoice> getInvoices() throws IOException {
    return database.getInvoices();
  }

  public Invoice getInvoiceById(Long id) throws IOException {
    return database.getInvoiceById(id);
  }

  public boolean updateInvoice(Long id, Invoice invoice) throws IOException {
    return database.updateInvoice(id, invoice);
  }

  public boolean removeInvoiceById(Long id) throws IOException {
    return database.removeInvoiceById(id);
  }
}
