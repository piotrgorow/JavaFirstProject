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
    if (invoice == null) {
      throw new IllegalArgumentException("Invoice cannot be null");
    }
    invoices.put(invoice.getId(), invoice);
  }

  @Override
  public Collection<Invoice> getInvoices() {
    return invoices.values();
  }

  @Override
  public Invoice getInvoiceById(String id) {
    if (findInvoice(id)) {
      return invoices.get(id);
    }
    return null;
  }

  @Override
  public boolean updateInvoice(String id, Invoice invoice) {
    if (invoice == null) {
      throw new IllegalArgumentException("Invoice cannot be null");
    }
    if (findInvoice(id)) {
      invoices.replace(id, invoice);
      return true;
    }
    return false;
  }

  @Override
  public void removeAllInvoices() {
    invoices.clear();
  }

  @Override
  public boolean removeInvoiceById(String id) {
    if (findInvoice(id)) {
      invoices.remove(id);
      return true;
    }
    return false;
  }

  private boolean findInvoice(String id) {
    return invoices.containsKey(id);
  }
}
