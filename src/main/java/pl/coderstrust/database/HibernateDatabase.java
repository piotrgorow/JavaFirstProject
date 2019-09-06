package pl.coderstrust.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-hibernate")
@Slf4j
public class HibernateDatabase implements Database {

  private InvoicesSpringDataRepository invoicesSpringDataRepository;

  public HibernateDatabase(InvoicesSpringDataRepository invoicesSpringDataRepository) {
    this.invoicesSpringDataRepository = invoicesSpringDataRepository;
  }

  @Override
  public void saveInvoice(Invoice invoice) {
    if (invoice == null) {
      String message = "Invoice cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    invoice.setId(null);
    Invoice result = invoicesSpringDataRepository.save(invoice);
    log.info("Writing invoice with ID = {}", result.getId());
  }

  @Override
  public Invoice getInvoiceById(Long id) {
    if (invoicesSpringDataRepository.findById(id).isPresent()) {
      log.info("Reading invoice with ID = {}", id);
      return invoicesSpringDataRepository.findById(id).get();
    }
    log.warn("Invoice with ID = {} does not exist", id);
    return null;
  }

  @Override
  public Collection<Invoice> getInvoices() {
    List<Invoice> result = new ArrayList<>();
    for (Invoice invoice : invoicesSpringDataRepository.findAll()) {
      result.add(invoice);
    }
    log.info("Reading all invoices");
    return result;
  }

  @Override
  public boolean updateInvoice(Long id, Invoice invoice) {
    if (invoice == null) {
      String message = "Invoice cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    if (invoicesSpringDataRepository.findById(id).isPresent()) {
      invoice.setId(id);
      invoicesSpringDataRepository.save(invoice);
      log.info("Updating invoice with ID = {}", id);
      return true;
    }
    log.warn("Update failed - invoice with ID = {} does not exist", id);
    return false;
  }

  @Override
  public boolean removeInvoiceById(Long id) {
    if (invoicesSpringDataRepository.findById(id).isPresent()) {
      log.info("Removing invoice with ID = {}", id);
      invoicesSpringDataRepository.deleteById(id);
      return true;
    }
    log.warn("Remove failed - invoice with ID = {} does not exist", id);
    return false;
  }
}
