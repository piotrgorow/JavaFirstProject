package pl.coderstrust.model;

import java.util.Collection;
import pl.coderstrust.database.InMemoryDatabase;

public class InvoiceBook {

  private final InMemoryDatabase database = new InMemoryDatabase();

  public void saveInvoice(Invoice invoice) {
    database.saveInvoice(invoice);
  }

  public Collection<Invoice> getInvoices() {
    return database.getInvoices();
  }
}
