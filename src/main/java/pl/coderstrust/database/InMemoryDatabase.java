package pl.coderstrust.database;

import java.util.Collection;
import java.util.HashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-memory")
public class InMemoryDatabase implements Database {

  private final HashMap<Long, Invoice> invoices;
  private Long databaseId = 1L;

  public InMemoryDatabase() {
    invoices = new HashMap<>();
  }

  @Override
  public void saveInvoice(Invoice invoice) {
    if (invoice == null) {
      throw new IllegalArgumentException("Invoice cannot be null");
    }
    invoice.setId(databaseId);
    invoices.put(invoice.getId(), invoice);
    databaseId++;
  }

  @Override
  public Collection<Invoice> getInvoices() {
    return invoices.values();
  }

  @Override
  public Invoice getInvoiceById(Long id) {
    if (findInvoice(id)) {
      return invoices.get(id);
    }
    return null;
  }

  @Override
  public boolean updateInvoice(Long id, Invoice invoice) {
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
  public boolean removeInvoiceById(Long id) {
    if (findInvoice(id)) {
      invoices.remove(id);
      return true;
    }
    return false;
  }

  private boolean findInvoice(Long id) {
    return invoices.containsKey(id);
  }
}
