package pl.coderstrust.database;

import java.util.Collection;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-memory")
@Slf4j
public class InMemoryDatabase implements Database {

  private final HashMap<Long, Invoice> invoices;
  private Long databaseId = 1L;

  public InMemoryDatabase() {
    invoices = new HashMap<>();
  }

  @Override
  public void saveInvoice(Invoice invoice) {
    if (invoice == null) {
      String message = "Invoice cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    invoice.setId(databaseId);
    invoices.put(invoice.getId(), invoice);
    log.info("Saving invoice with ID = {}", invoice.getId());
    databaseId++;
  }

  @Override
  public Collection<Invoice> getInvoices() {
    log.info("Reading all invoices");
    return invoices.values();
  }

  @Override
  public Invoice getInvoiceById(Long id) {
    if (findInvoice(id)) {
      log.info("Reading invoice with ID = {}", id);
      return invoices.get(id);
    }
    log.warn("Invoice with ID = {} does not exist", id);
    return null;
  }

  @Override
  public boolean updateInvoice(Long id, Invoice invoice) {
    if (invoice == null) {
      String message = "Invoice cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    if (findInvoice(id)) {
      log.info("Updating invoice with ID = {}", id);
      invoices.replace(id, invoice);
      return true;
    }
    log.warn("Update failed - invoice with ID = {} does not exist", id);
    return false;
  }

  @Override
  public boolean removeInvoiceById(Long id) {
    if (findInvoice(id)) {
      log.info("Removing invoice with ID = {}", id);
      invoices.remove(id);
      return true;
    }
    log.warn("Remove failed - invoice with ID = {} does not exist", id);
    return false;
  }

  private boolean findInvoice(Long id) {
    log.info("Checking if an invoice with ID = {} exists", id);
    return invoices.containsKey(id);
  }
}
