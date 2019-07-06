package pl.coderstrust.database;

import java.io.IOException;
import java.util.Collection;
import pl.coderstrust.model.Invoice;

public interface Database {

  void saveInvoice(Invoice invoice) throws IOException;

  Invoice getInvoiceById(Long id) throws IOException;

  Collection<Invoice> getInvoices() throws IOException;

  boolean updateInvoice(Long id, Invoice invoice) throws IOException;

  boolean removeInvoiceById(Long id) throws IOException;
}
