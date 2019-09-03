package pl.coderstrust.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-hibernate")
public class HibernateDatabase implements Database {

  private InvoicesSpringDataRepository invoicesSpringDataRepository;

  public HibernateDatabase(InvoicesSpringDataRepository invoicesSpringDataRepository) {
    this.invoicesSpringDataRepository = invoicesSpringDataRepository;
  }

  @Override
  public void saveInvoice(Invoice invoice) {
    if (invoice == null) {
      throw new IllegalArgumentException("Invoice cannot be null");
    }
    invoice.setId(null);
    invoicesSpringDataRepository.save(invoice);
  }

  @Override
  public Invoice getInvoiceById(Long id) {
    if (invoicesSpringDataRepository.findById(id).isPresent()) {
      return invoicesSpringDataRepository.findById(id).get();
    }
    return null;
  }

  @Override
  public Collection<Invoice> getInvoices() {
    List<Invoice> result = new ArrayList<>();
    for (Invoice invoice : invoicesSpringDataRepository.findAll()) {
      result.add(invoice);
    }
    return result;
  }

  @Override
  public boolean updateInvoice(Long id, Invoice invoice) {
    if (invoice == null) {
      throw new IllegalArgumentException("Invoice cannot be null");
    }
    if (invoicesSpringDataRepository.findById(id).isPresent()) {
      invoice.setId(id);
      invoicesSpringDataRepository.save(invoice);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeInvoiceById(Long id) {
    if (invoicesSpringDataRepository.findById(id).isPresent()) {
      invoicesSpringDataRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
