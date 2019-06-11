package pl.coderstrust.database;

import java.util.List;
import pl.coderstrust.model.Invoice;

public class InMemoryDatabase implements Database {

  private List<Invoice> invoices;

  @Override
  public void saveInvoice(Invoice invoice) {
    invoices.add(invoice);
  }

  @Override
  public List<Invoice> getInvoices() {
    return invoices;
  }

  @Override
  public Invoice getInvoiceById(String id) {
    return null;
  }

  @Override
  public void updateInvoice(Invoice invoice) {

  }
}
