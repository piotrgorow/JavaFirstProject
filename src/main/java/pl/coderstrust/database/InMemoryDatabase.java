package pl.coderstrust.database;

import java.util.Collection;
import java.util.HashMap;
import pl.coderstrust.model.Invoice;

public class InMemoryDatabase implements Database {

  private final HashMap<String, Invoice> invoices;

  public InMemoryDatabase() {
    invoices = new HashMap<>();
  }

  @Override
  public void saveInvoice(Invoice invoice) {
    invoices.put(invoice.getId(), invoice);
  }

  @Override
  public Collection<Invoice> getInvoices() {
    return invoices.values();
  }

  @Override
  public Invoice getInvoiceById(String id) {
    if (findInvoide(id)) {
      return invoices.get(id);
    }
    return null;
  }

  @Override
  public boolean updateInvoice(String id, Invoice invoice) {
    if (findInvoide(id)) {
      invoices.replace(id, invoice);
      return true;
    }
    return false;
  }

  private boolean findInvoide(String id) {
    return invoices.containsKey(id);
  }
}
