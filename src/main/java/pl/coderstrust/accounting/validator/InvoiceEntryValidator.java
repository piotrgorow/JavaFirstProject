package pl.coderstrust.accounting.validator;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import pl.coderstrust.accounting.Utils;
import pl.coderstrust.model.InvoiceEntry;

@Slf4j
public class InvoiceEntryValidator implements Validator<InvoiceEntry> {

  @Override
  public List<String> validate(InvoiceEntry invoiceEntry) {
    List<String> result = new LinkedList<>();
    if (Utils.isNullOrEmpty(invoiceEntry.getDescription())) {
      log.warn("Parameter description cannot be null or empty");
      result.add("Invoice entry description must not be null or empty");
    }
    if (invoiceEntry.getQuantity() < 1) {
      log.warn("Parameter quantity must be positive value");
      result.add("Quantity must be positive value");
    }
    if (invoiceEntry.getValue().compareTo(new BigDecimal(0.0)) < 0) {
      log.warn("Parameter value must not be negative number");
      result.add("Invoice entry value must not be negative number");
    }
    return result;
  }
}
