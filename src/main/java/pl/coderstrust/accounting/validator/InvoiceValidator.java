package pl.coderstrust.accounting.validator;

import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.coderstrust.accounting.Utils;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

@Component
@Slf4j
public class InvoiceValidator implements Validator<Invoice> {

  private Validator<Company> companyValidator = new CompanyValidator();
  private Validator<InvoiceEntry> invoiceEntryValidator = new InvoiceEntryValidator();

  @Override
  public List<String> validate(Invoice invoice) {
    List<String> result = new LinkedList<>();
    if (Utils.isNullOrEmpty(invoice.getInvoiceNumber())) {
      log.warn("Invoice number must not be null or empty");
      result.add("Invoice number must not be null or empty");
    }
    if (invoice.getBuyer() == null) {
      log.warn("Buyer must not be null");
      result.add("Buyer must not be null");
    } else {
      result.addAll(companyValidator.validate(invoice.getBuyer()));
    }
    if (invoice.getSeller() == null) {
      log.warn("Seller must not be null");
      result.add("Seller must not be null");
    } else {
      result.addAll(companyValidator.validate(invoice.getSeller()));
    }
    if (invoice.getDate() == null) {
      log.warn("Invoice date must not be null");
      result.add("Invoice date must not be null");
    }
    if (invoice.getInvoiceEntries() == null || invoice.getInvoiceEntries().isEmpty()) {
      log.warn("Invoice entries must not be null or empty");
      result.add("Invoice entries must not be null or empty");
    } else {
      for (InvoiceEntry invoiceEntry : invoice.getInvoiceEntries()) {
        result.addAll(invoiceEntryValidator.validate(invoiceEntry));
      }
    }
    return result;
  }
}
