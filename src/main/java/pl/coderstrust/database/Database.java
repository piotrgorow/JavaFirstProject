package pl.coderstrust.database;

import java.util.Collection;
import pl.coderstrust.model.Invoice;

public interface Database {

  void saveInvoice(Invoice invoice);

  Invoice getInvoiceById(String id);

  Collection<Invoice> getInvoices();

  boolean updateInvoice(String id, Invoice invoice);

  boolean removeInvoiceById(String id);
}
