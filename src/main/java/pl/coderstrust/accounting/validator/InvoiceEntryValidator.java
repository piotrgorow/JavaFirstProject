package pl.coderstrust.accounting.validator;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import pl.coderstrust.accounting.Utils;
import pl.coderstrust.model.InvoiceEntry;

public class InvoiceEntryValidator implements Validator<InvoiceEntry> {

  @Override
  public List<String> validate(InvoiceEntry invoiceEntry) {
    List<String> result = new LinkedList<>();
    if (Utils.isNullOrEmpty(invoiceEntry.getDescription())) {
      result.add("Invoice entry description must not be null or empty");
    }
    if (invoiceEntry.getQuantity() < 1) {
      result.add("Quantity must be positive value");
    }
    if (invoiceEntry.getValue().compareTo(new BigDecimal(0.0)) < 0) {
      result.add("Invoice entry value must not be negative number");
    }
    return result;
  }
}
