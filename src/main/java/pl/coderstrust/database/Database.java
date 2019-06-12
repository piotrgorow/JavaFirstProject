package pl.coderstrust.database;

import java.util.List;
import pl.coderstrust.model.Invoice;

public interface Database {

  void saveInvoice(Invoice invoice);

  Invoice getInvoiceById(String id);

  List<Invoice> getInvoices();

  void updateInvoice(Invoice invoice);
}
