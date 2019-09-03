package pl.coderstrust.accounting.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

class InvoiceEntryValidatorTest {

  private static final Validator<InvoiceEntry> validator = new InvoiceEntryValidator();

  @Test
  @DisplayName("Should validate correct invoice entry")
  void shouldValidateCorrectInvoiceEntry() {
    // When
    InvoiceEntry invoiceEntry = new InvoiceEntry(1L, "Sok", 10, BigDecimal.valueOf(10.50), Vat.VAT_23);
    List<String> result = InvoiceEntryValidatorTest.validator.validate(invoiceEntry);

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should validate invoice entry with null description")
  void shouldValidateInvoiceEntryWithNullDescription() {
    // When
    InvoiceEntry invoiceEntry = new InvoiceEntry(1L, null, 10, BigDecimal.valueOf(50.00), Vat.VAT_23);
    String expected = "Invoice entry description must not be null or empty";
    List<String> result = InvoiceEntryValidatorTest.validator.validate(invoiceEntry);

    // Then
    assertEquals(expected, result.get(0));
  }

  @Test
  @DisplayName("Should validate invoice entry with empty description")
  void shouldValidateInvoiceEntryWithEmptyDescription() {
    // When
    InvoiceEntry invoiceEntry = new InvoiceEntry(1L, "", 10, BigDecimal.valueOf(50.00), Vat.VAT_23);
    String expected = "Invoice entry description must not be null or empty";
    List<String> result = InvoiceEntryValidatorTest.validator.validate(invoiceEntry);

    // Then
    assertEquals(expected, result.get(0));
  }

  @ParameterizedTest
  @MethodSource("invoiceEntryArgumentsQuantity")
  @DisplayName("Should validate invoice entry with non-positive quantity")
  void shouldValidateInvoiceEntryWithNonPositiveQuantity(InvoiceEntry invoiceEntry) {
    // When
    String expected = "Quantity must be positive value";
    List<String> result = InvoiceEntryValidatorTest.validator.validate(invoiceEntry);

    // Then
    assertEquals(expected, result.get(0));
  }

  @ParameterizedTest
  @MethodSource("invoiceEntryArgumentsValue")
  @DisplayName("Should validate invoice entry with negative value")
  void shouldValidateInvoiceEntryWithNegativeValue(InvoiceEntry invoiceEntry) {
    // When
    String expected = "Invoice entry value must not be negative number";
    List<String> result = InvoiceEntryValidatorTest.validator.validate(invoiceEntry);

    // Then
    assertEquals(expected, result.get(0));
  }

  private static Stream<Arguments> invoiceEntryArgumentsQuantity() {
    return Stream.of(
        Arguments.of(new InvoiceEntry(1L, "Sok", 0, BigDecimal.valueOf(25.55), Vat.VAT_23)),
        Arguments.of(new InvoiceEntry(1L, "Sok", -10, BigDecimal.valueOf(25.55), Vat.VAT_23)),
        Arguments.of(new InvoiceEntry(1L, "Sok", -100, BigDecimal.valueOf(25.55), Vat.VAT_23))
    );
  }

  private static Stream<Arguments> invoiceEntryArgumentsValue() {
    return Stream.of(
        Arguments.of(new InvoiceEntry(1L, "Sok", 10, BigDecimal.valueOf(-1.0), Vat.VAT_23)),
        Arguments.of(new InvoiceEntry(1L, "Sok", 10, BigDecimal.valueOf(-20.09), Vat.VAT_23)),
        Arguments.of(new InvoiceEntry(1L, "Sok", 10, BigDecimal.valueOf(-100.56), Vat.VAT_23))
    );
  }
}
