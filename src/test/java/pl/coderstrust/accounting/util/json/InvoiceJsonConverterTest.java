package pl.coderstrust.accounting.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.Vat;

class InvoiceJsonConverterTest {

  @ParameterizedTest
  @MethodSource("invoicesParameters")
  @DisplayName("Should return correct JSON when invoice object was passed")
  void shouldReturnCorrectJsonStringWhenInvoiceObjectIsPassed(Invoice given, String expected) throws IOException {
    // When
    String result = InvoiceJsonConverter.toJson(given);

    // Then
    assertEquals(expected, result);
  }

  private static Stream<Arguments> invoicesParameters() {
    return Stream.of(
        Arguments.of(new Invoice("1", LocalDate.of(2019, 6, 14),
                new Company("Seller", "VAT_23", "ul. Krótka"),
                new Company("Buyer", "VAT_23", "ul. Długa")),
            "{\"id\":\"1\",\"date\":\"2019-06-14\",\"seller\":{\"name\":\"Seller\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":{\"name\":\"Buyer\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},"
                + "\"invoiceEntries\":[]}"),
        Arguments.of(new Invoice(null, LocalDate.of(2019, 6, 14),
                new Company("Seller", "VAT_23", "ul. Krótka"),
                new Company("Buyer", "VAT_23", "ul. Długa")),
            "{\"id\":null,\"date\":\"2019-06-14\",\"seller\":{\"name\":\"Seller\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":{\"name\":\"Buyer\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},"
                + "\"invoiceEntries\":[]}"),
        Arguments.of(new Invoice("1", null,
                new Company("Seller", "VAT_23", "ul. Krótka"),
                new Company("Buyer", "VAT_23", "ul. Długa")),
            "{\"id\":\"1\",\"date\":null,\"seller\":{\"name\":\"Seller\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":{\"name\":\"Buyer\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},"
                + "\"invoiceEntries\":[]}"),
        Arguments.of(new Invoice("1", LocalDate.of(2019, 6, 14),
                null,
                new Company("Buyer", "VAT_23", "ul. Długa")),
            "{\"id\":\"1\",\"date\":\"2019-06-14\",\"seller\":null,\"buyer\":{\"name\":\"Buyer\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},"
                + "\"invoiceEntries\":[]}"),
        Arguments.of(new Invoice("1", LocalDate.of(2019, 6, 14),
                new Company("Seller", "VAT_23", "ul. Krótka"),
                null),
            "{\"id\":\"1\",\"date\":\"2019-06-14\",\"seller\":{\"name\":\"Seller\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":null,"
                + "\"invoiceEntries\":[]}")
    );
  }

  @Test
  @DisplayName("Should return proper JSON when invoice elements was added")
  void shouldReturnProperJasonStringWhenInvoiceElementsWasAdded() throws IOException {
    // Given
    Invoice invoice = new Invoice("1", LocalDate.of(2019, 6, 14),
        new Company("Seller", "VAT_23", "ul. Krótka"),
        new Company("Buyer", "VAT_23", "ul. Długa"));
    invoice.addInvoiceEntry("Mąka", 1, BigDecimal.valueOf(10.4), Vat.VAT_23);
    invoice.addInvoiceEntry("Cukier", 2, BigDecimal.valueOf(3.5), Vat.VAT_23);
    invoice.addInvoiceEntry("Sól", 3, BigDecimal.valueOf(5.64), Vat.VAT_23);
    String expected = "{\"id\":\"1\",\"date\":\"2019-06-14\",\"seller\":{\"name\":\"Seller\","
        + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":{\"name\":\"Buyer\","
        + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},\"invoiceEntries\":"
        + "[{\"description\":\"Mąka\",\"quantity\":1,\"value\":10.4,\"vatRate\":\"VAT_23\",\"vatValue\":2.392},"
        + "{\"description\":\"Cukier\",\"quantity\":2,\"value\":3.5,\"vatRate\":\"VAT_23\",\"vatValue\":0.805},"
        + "{\"description\":\"Sól\",\"quantity\":3,\"value\":5.64,\"vatRate\":\"VAT_23\",\"vatValue\":1.2972}]}";

    // When
    String result = InvoiceJsonConverter.toJson(invoice);

    // Then
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Should throw exception when passed parameter invoice is null")
  void shouldThrowExceptionWhenPassedParameterInvoiceIsNull() {
    assertThrows(IllegalArgumentException.class, () -> InvoiceJsonConverter.toJson(null));
  }

  @ParameterizedTest
  @MethodSource("jasonParameters")
  @DisplayName("Should return correct invoice object when JSON was passed")
  void shouldReturnCorrectInvoiceObjectWhenJsonStringIsPassed(String given, Invoice expected) throws IOException {
    // When
    Invoice result = InvoiceJsonConverter.fromJson(given);

    // Then
    assertEquals(expected, result);
  }

  private static Stream<Arguments> jasonParameters() {
    return Stream.of(
        Arguments.of("{\"id\":\"1\",\"date\":\"2019-06-14\",\"seller\":{\"name\":\"Seller\","
            + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":{\"name\":\"Buyer\","
            + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},"
            + "\"invoiceEntries\":[]}", new Invoice("1", LocalDate.of(2019, 6, 14),
            new Company("Seller", "VAT_23", "ul. Krótka"),
            new Company("Buyer", "VAT_23", "ul. Długa"))),
        Arguments.of("{\"id\":null,\"date\":\"2019-06-14\",\"seller\":{\"name\":\"Seller\","
            + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":{\"name\":\"Buyer\","
            + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},"
            + "\"invoiceEntries\":[]}", new Invoice(null, LocalDate.of(2019, 6, 14),
            new Company("Seller", "VAT_23", "ul. Krótka"),
            new Company("Buyer", "VAT_23", "ul. Długa"))),
        Arguments.of("{\"id\":\"1\",\"date\":null,\"seller\":{\"name\":\"Seller\","
            + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":{\"name\":\"Buyer\","
            + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},"
            + "\"invoiceEntries\":[]}", new Invoice("1", null,
            new Company("Seller", "VAT_23", "ul. Krótka"),
            new Company("Buyer", "VAT_23", "ul. Długa"))),
        Arguments.of("{\"id\":\"1\",\"date\":\"2019-06-14\",\"seller\":null,\"buyer\":{\"name\":\"Buyer\","
            + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},"
            + "\"invoiceEntries\":[]}", new Invoice("1", LocalDate.of(2019, 6, 14),
            null,
            new Company("Buyer", "VAT_23", "ul. Długa"))),
        Arguments.of("{\"id\":\"1\",\"date\":\"2019-06-14\",\"seller\":{\"name\":\"Seller\","
            + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":null,"
            + "\"invoiceEntries\":[]}", new Invoice("1", LocalDate.of(2019, 6, 14),
            new Company("Seller", "VAT_23", "ul. Krótka"),
            null))
    );
  }

  @Test
  @DisplayName("Should return proper invoice object when invoice elements was added")
  void shouldReturnProperInvoiceObjectWhenInvoiceElementsWasAdded() throws IOException {
    // Given
    String given = "{\"id\":\"1\",\"date\":\"2019-06-14\",\"seller\":{\"name\":\"Seller\","
        + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":{\"name\":\"Buyer\","
        + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},\"invoiceEntries\":"
        + "[{\"description\":\"Mąka\",\"quantity\":1,\"value\":10.4,\"vatRate\":\"VAT_23\",\"vatValue\":2.392},"
        + "{\"description\":\"Cukier\",\"quantity\":2,\"value\":3.5,\"vatRate\":\"VAT_23\",\"vatValue\":0.805},"
        + "{\"description\":\"Sól\",\"quantity\":3,\"value\":5.64,\"vatRate\":\"VAT_23\",\"vatValue\":1.2972}]}";
    Invoice expected = new Invoice("1", LocalDate.of(2019, 6, 14),
        new Company("Seller", "VAT_23", "ul. Krótka"),
        new Company("Buyer", "VAT_23", "ul. Długa"));
    expected.addInvoiceEntry("Mąka", 1, BigDecimal.valueOf(10.4), Vat.VAT_23);
    expected.addInvoiceEntry("Cukier", 2, BigDecimal.valueOf(3.5), Vat.VAT_23);
    expected.addInvoiceEntry("Sól", 3, BigDecimal.valueOf(5.64), Vat.VAT_23);

    // When
    Invoice result = InvoiceJsonConverter.fromJson(given);

    // Then
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Should throw exception when passed parameter JSON is null")
  void shouldThrowExceptionWhenPassedParameterJsonIsNull() {
    assertThrows(IllegalArgumentException.class, () -> InvoiceJsonConverter.fromJson(null));
  }

  @Test
  @DisplayName("Should throw exception when passed parameter JSON is empty")
  void shouldThrowExceptionWhenPassedParameterJsonIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> InvoiceJsonConverter.fromJson(""));
  }
}
