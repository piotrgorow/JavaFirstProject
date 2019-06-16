package pl.coderstrust.accounting.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
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
  void shouldReturnProperJasonStringWhenInvoiceElementsWasAdded() throws IOException {
    //given
    Invoice invoice = new Invoice("1", LocalDate.of(2019, 6, 14),
        new Company("Seller", "VAT_23", "ul. Krótka"),
        new Company("Buyer", "VAT_23", "ul. Długa"));
    invoice.addInvoiceEntry("Mąka", BigDecimal.valueOf(10.4), Vat.VAT_23);
    invoice.addInvoiceEntry("Cukier", BigDecimal.valueOf(3.5), Vat.VAT_23);
    invoice.addInvoiceEntry("Sól", BigDecimal.valueOf(5.64), Vat.VAT_23);
    String expected = "{\"id\":\"1\",\"date\":\"2019-06-14\",\"seller\":{\"name\":\"Seller\","
        + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":{\"name\":\"Buyer\","
        + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},\"invoiceEntries\":"
        + "[{\"description\":\"Mąka\",\"value\":10.4,\"vatRate\":\"VAT_23\",\"vatValue\":2.392},{\"description\":"
        + "\"Cukier\",\"value\":3.5,\"vatRate\":\"VAT_23\",\"vatValue\":0.805},{\"description\":\"Sól\",\"value\":5"
        + ".64,\"vatRate\":\"VAT_23\",\"vatValue\":1.2972}]}";

    //when
    String result = InvoiceJsonConverter.toJson(invoice);

    //then
    assertEquals(expected, result);
  }

  @Test
  void shouldThrowExceptionWhenPassedParameterInvoiceIsNull() {
    assertThrows(IllegalArgumentException.class, () -> InvoiceJsonConverter.toJson(null));
  }

  @ParameterizedTest
  @MethodSource("jasonParameters")
  void shouldReturnCorrectInvoiceObjectWhenJsonStringIsPassed(String given, Invoice expected) throws IOException {
    //when
    Invoice result = InvoiceJsonConverter.fromJson(given);

    //then
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
  void shouldReturnProperInvoiceObjectWhenInvoiceElementsWasAdded() throws IOException {
    //given
    String given = "{\"id\":\"1\",\"date\":\"2019-06-14\",\"seller\":{\"name\":\"Seller\","
        + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Krótka\"},\"buyer\":{\"name\":\"Buyer\","
        + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":\"ul. Długa\"},\"invoiceEntries\":"
        + "[{\"description\":\"Mąka\",\"value\":10.4,\"vatRate\":\"VAT_23\",\"vatValue\":2.392},{\"description\":"
        + "\"Cukier\",\"value\":3.5,\"vatRate\":\"VAT_23\",\"vatValue\":0.805},{\"description\":\"Sól\",\"value\":5"
        + ".64,\"vatRate\":\"VAT_23\",\"vatValue\":1.2972}]}";
    Invoice expected = new Invoice("1", LocalDate.of(2019, 6, 14),
        new Company("Seller", "VAT_23", "ul. Krótka"),
        new Company("Buyer", "VAT_23", "ul. Długa"));
    expected.addInvoiceEntry("Mąka", BigDecimal.valueOf(10.4), Vat.VAT_23);
    expected.addInvoiceEntry("Cukier", BigDecimal.valueOf(3.5), Vat.VAT_23);
    expected.addInvoiceEntry("Sól", BigDecimal.valueOf(5.64), Vat.VAT_23);

    //when
    Invoice result = InvoiceJsonConverter.fromJson(given);

    //then
    assertEquals(expected, result);
  }

  @Test
  void shouldThrowExceptionWhenPassedParameterJsonIsEmpty() {
    assertThrows(IllegalArgumentException.class, () -> InvoiceJsonConverter.fromJson(""));
  }

  @Test
  void shouldThrowExceptionWhenPassedParameterJsonIsNull() {
    assertThrows(IllegalArgumentException.class, () -> InvoiceJsonConverter.fromJson(null));
  }
}
