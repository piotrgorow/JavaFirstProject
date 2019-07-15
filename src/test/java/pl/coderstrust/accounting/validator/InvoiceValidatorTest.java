package pl.coderstrust.accounting.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.coderstrust.database.InvoiceTestUtil;
import pl.coderstrust.model.Invoice;

class InvoiceValidatorTest {

  private static final Validator<Invoice> validator = new InvoiceValidator();

  @Test
  @DisplayName("Should validate correct invoice")
  void shouldValidateCorrectInvoice() {
    // When
    Invoice invoice = InvoiceTestUtil.sampleInvoice();
    List<String> result = InvoiceValidatorTest.validator.validate(invoice);

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should validate invoice with empty invoice number")
  void shouldValidateInvoiceWithEmptyInvoiceNumber() {
    // When
    Invoice invoice = InvoiceTestUtil.sampleInvoiceWIthNoInvoiceNumber();
    String expected = "Invoice number must not be null or empty";
    List<String> result = InvoiceValidatorTest.validator.validate(invoice);

    // Then
    assertEquals(expected, result.get(0));
  }

  @Test
  @DisplayName("Should validate invoice with empty date")
  void shouldValidateInvoiceWithEmptyInvoiceDate() {
    // When
    Invoice invoice = InvoiceTestUtil.sampleInvoiceWIthNoDate();
    String expected = "Invoice date must not be null";
    List<String> result = InvoiceValidatorTest.validator.validate(invoice);

    // Then
    assertEquals(expected, result.get(0));
  }

  @Test
  @DisplayName("Should validate invoice with no seller")
  void shouldValidateInvoiceWithNoSeller() {
    // When
    Invoice invoice = InvoiceTestUtil.sampleInvoiceWIthNoSeller();
    String expected = "Seller must not be null";
    List<String> result = InvoiceValidatorTest.validator.validate(invoice);

    // Then
    assertEquals(expected, result.get(0));
  }

  @Test
  @DisplayName("Should validate invoice with no buyer")
  void shouldValidateInvoiceWithNoBuyer() {
    // When
    Invoice invoice = InvoiceTestUtil.sampleInvoiceWIthNoBuyer();
    String expected = "Buyer must not be null";
    List<String> result = InvoiceValidatorTest.validator.validate(invoice);

    // Then
    assertEquals(expected, result.get(0));
  }
}
