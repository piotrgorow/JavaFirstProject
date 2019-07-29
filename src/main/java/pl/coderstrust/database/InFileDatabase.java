package pl.coderstrust.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import pl.coderstrust.accounting.util.json.InvoiceJsonConverter;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-file")
public class InFileDatabase implements Database {

  private final Map<Long, Invoice> invoices;
  private final FileHelper fileHelper;
  private final InvoiceJsonConverter invoiceJsonConverter;
  private final List<String> invoiceToSave = new ArrayList<>();
  private final InFileDatabaseProperties properties;
  private Long databaseId;

  @Autowired
  InFileDatabase(FileHelper fileHelper, InvoiceJsonConverter invoiceJsonConverter,
      InFileDatabaseProperties properties) throws IOException {
    this.fileHelper = fileHelper;
    this.invoiceJsonConverter = invoiceJsonConverter;
    this.properties = properties;
    invoices = new HashMap<>();
    fileHelper.checkFilesExistence(properties.getFilePath());
    String lastLine = fileHelper.getLastLine(properties.getFilePath());
    if (lastLine.equals("")) {
      databaseId = 0L;
    } else {
      databaseId = invoiceJsonConverter.fromJson(lastLine).getId();
    }
  }

  @Override
  public void saveInvoice(Invoice invoice) throws IOException {
    if (invoice == null) {
      throw new IllegalArgumentException("Invoice cannot be null");
    }
    invoiceToSave.clear();
    invoice.setId(databaseId + 1);
    invoiceToSave.add(invoiceJsonConverter.toJson(invoice));
    fileHelper.writeLines(invoiceToSave, properties.getFilePath(), true);
    databaseId = invoice.getId();
  }

  @Override
  public Invoice getInvoiceById(Long id) throws IOException {
    loadHashMap();
    if (findInvoice(id)) {
      return invoices.get(id);
    }
    return null;
  }

  @Override
  public Collection<Invoice> getInvoices() throws IOException {
    loadHashMap();
    return invoices.values();
  }

  @Override
  public boolean updateInvoice(Long id, Invoice invoice) throws IOException {
    loadHashMap();
    if (invoice == null) {
      throw new IllegalArgumentException("Invoice cannot be null");
    }
    if (findInvoice(id)) {
      invoice.setId(id);
      invoices.replace(id, invoice);
      saveHashMapToDatabase();
      return true;
    }
    return false;
  }

  @Override
  public boolean removeInvoiceById(Long id) throws IOException {
    loadHashMap();
    if (findInvoice(id)) {
      invoices.remove(id);
      saveHashMapToDatabase();
      return true;
    }
    return false;
  }

  private boolean findInvoice(Long id) {
    return invoices.containsKey(id);
  }

  private void loadHashMap() throws IOException {
    invoices.clear();
    List<String> invoicesFromFile = fileHelper.readLines(properties.getFilePath());
    Invoice jsonInvoice;
    for (String inv : invoicesFromFile) {
      jsonInvoice = invoiceJsonConverter.fromJson(inv);
      invoices.put(jsonInvoice.getId(), jsonInvoice);
    }
  }

  private void saveHashMapToDatabase() throws IOException {
    invoiceToSave.clear();
    for (Invoice inv : invoices.values()) {
      invoiceToSave.add(invoiceJsonConverter.toJson(inv));
    }
    fileHelper.writeLines(invoiceToSave, properties.getFilePath(), false);
  }
}
