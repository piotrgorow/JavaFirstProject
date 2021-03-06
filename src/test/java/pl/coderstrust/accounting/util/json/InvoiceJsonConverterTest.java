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
import pl.coderstrust.database.InvoiceTestUtil;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.Vat;

class InvoiceJsonConverterTest {

  @ParameterizedTest
  @MethodSource("invoicesParameters")
  @DisplayName("Should return correct JSON when invoice object was passed")
  void shouldReturnCorrectJsonStringWhenInvoiceObjectIsPassed(Invoice given, String expected)
      throws IOException {
    // When
    InvoiceJsonConverter invoiceJsonConverter = new InvoiceJsonConverter();
    String result = invoiceJsonConverter.toJson(given);

    // Then
    assertEquals(expected, result);
  }

  private static Stream<Arguments> invoicesParameters() {
    return Stream.of(
        Arguments.of(new Invoice("1", LocalDate.of(2019, 6, 14),
                new Company(1L, "Seller", "VAT_23", InvoiceTestUtil.sampleAddress1()),
                new Company(1L, "Buyer", "VAT_23", InvoiceTestUtil.sampleAddress1())),
            "{\"id\":null,\"mongoId\":null,\"invoiceNumber\":\"1\",\"date\":\"2019-06-14\",\"seller\":"
                + "{\"companyId\":1,\"name\":\"Seller\",\"taxIdentificationNumber\":\"VAT_23\","
                + "\"address\":{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\",\"postalCode\":\"00-123\","
                + "\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},\"buyer\":{\"companyId\":1,\"name\":\"Buyer\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":{\"addressId\":1,\"streetAddress\":"
                + "\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},"
                + "\"invoiceEntries\":[]}"),
        Arguments.of(new Invoice(null, LocalDate.of(2019, 6, 14),
                new Company(1L, "Seller", "VAT_23", InvoiceTestUtil.sampleAddress1()),
                new Company(1L, "Buyer", "VAT_23", InvoiceTestUtil.sampleAddress1())),
            "{\"id\":null,\"mongoId\":null,\"invoiceNumber\":null,\"date\":\"2019-06-14\",\"seller\":"
                + "{\"companyId\":1,\"name\":\"Seller\",\"taxIdentificationNumber\":\"VAT_23\",\"address\":"
                + "{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\","
                + "\"countryCode\":\"PL\"}},\"buyer\":{\"companyId\":1,\"name\":\"Buyer\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":{\"addressId\":1,\"streetAddress\":"
                + "\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},"
                + "\"invoiceEntries\":[]}"),
        Arguments.of(new Invoice("1", null,
                new Company(1L, "Seller", "VAT_23", InvoiceTestUtil.sampleAddress1()),
                new Company(1L, "Buyer", "VAT_23", InvoiceTestUtil.sampleAddress1())),
            "{\"id\":null,\"mongoId\":null,\"invoiceNumber\":\"1\",\"date\":null,\"seller\":{\"companyId\":1,\"name\":"
                + "\"Seller\",\"taxIdentificationNumber\":\"VAT_23\",\"address\":{\"addressId\":1,\"streetAddress\":"
                + "\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},"
                + "\"buyer\":{\"companyId\":1,\"name\":\"Buyer\",\"taxIdentificationNumber\":\"VAT_23\",\"address\":"
                + "{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\",\"postalCode\":\"00-123\","
                + "\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},\"invoiceEntries\":[]}"),
        Arguments.of(new Invoice("1", LocalDate.of(2019, 6, 14),
                null,
                new Company(1L, "Buyer", "VAT_23", InvoiceTestUtil.sampleAddress1())),
            "{\"id\":null,\"mongoId\":null,\"invoiceNumber\":\"1\",\"date\":\"2019-06-14\",\"seller\":null,"
                + "\"buyer\":{\"companyId\":1,\"name\":\"Buyer\","
                + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":{\"addressId\":1,\"streetAddress\":"
                + "\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},"
                + "\"invoiceEntries\":[]}"),
        Arguments.of(new Invoice("1", LocalDate.of(2019, 6, 14),
                new Company(1L, "Seller", "VAT_23", InvoiceTestUtil.sampleAddress1()),
                null),
            "{\"id\":null,\"mongoId\":null,\"invoiceNumber\":\"1\",\"date\":\"2019-06-14\",\"seller\":"
                + "{\"companyId\":1,\"name\":\"Seller\",\"taxIdentificationNumber\":\"VAT_23\","
                + "\"address\":{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\",\"postalCode\":\"00-123\","
                + "\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},\"buyer\":null,\"invoiceEntries\":[]}")
    );
  }

  @Test
  @DisplayName("Should return proper JSON when invoice elements was added")
  void shouldReturnProperJasonStringWhenInvoiceElementsWasAdded() throws IOException {
    // Given
    Invoice invoice = new Invoice("1", LocalDate.of(2019, 6, 14),
        new Company(1L, "Seller", "VAT_23", InvoiceTestUtil.sampleAddress1()),
        new Company(1L, "Buyer", "VAT_23", InvoiceTestUtil.sampleAddress1()));
    invoice.addInvoiceEntry(1L, "Mąka", 1, BigDecimal.valueOf(10.4), Vat.VAT_23);
    invoice.addInvoiceEntry(2L, "Cukier", 2, BigDecimal.valueOf(3.5), Vat.VAT_23);
    invoice.addInvoiceEntry(3L, "Sól", 3, BigDecimal.valueOf(5.64), Vat.VAT_23);
    String expected = "{\"id\":null,\"mongoId\":null,\"invoiceNumber\":\"1\",\"date\":\"2019-06-14\","
        + "\"seller\":{\"companyId\":1,\"name\":\"Seller\","
        + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\","
        + "\"postalCode\":\"00-123\",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},"
        + "\"buyer\":{\"companyId\":1,\"name\":\"Buyer\",\"taxIdentificationNumber\":\"VAT_23\","
        + "\"address\":{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\",\"postalCode\":\"00-123\""
        + ",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},\"invoiceEntries\":"
        + "[{\"invoiceEntryId\":1,\"description\":\"Mąka\",\"quantity\":1,\"value\":10.4000,\"vatRate\":\"VAT_23\","
        + "\"vatValue\":2.3920},{\"invoiceEntryId\":2,\"description\":\"Cukier\",\"quantity\":2,\"value\":3.5000,"
        + "\"vatRate\":\"VAT_23\",\"vatValue\":0.8050},{\"invoiceEntryId\":3,\"description\":\"Sól\",\"quantity\":3,"
        + "\"value\":5.6400,\"vatRate\":\"VAT_23\",\"vatValue\":1.2972}]}";

    // When
    InvoiceJsonConverter invoiceJsonConverter = new InvoiceJsonConverter();
    String result = invoiceJsonConverter.toJson(invoice);

    // Then
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Should throw exception when passed parameter invoice is null")
  void shouldThrowExceptionWhenPassedParameterInvoiceIsNull() {
    InvoiceJsonConverter invoiceJsonConverter = new InvoiceJsonConverter();
    assertThrows(IllegalArgumentException.class, () -> invoiceJsonConverter.toJson(null));
  }

  @ParameterizedTest
  @MethodSource("jasonParameters")
  @DisplayName("Should return correct invoice object when JSON was passed")
  void shouldReturnCorrectInvoiceObjectWhenJsonStringIsPassed(String given, Invoice expected)
      throws IOException {
    // When
    InvoiceJsonConverter invoiceJsonConverter = new InvoiceJsonConverter();
    Invoice result = invoiceJsonConverter.fromJson(given);

    // Then
    assertEquals(expected, result);
  }

  private static Stream<Arguments> jasonParameters() {
    return Stream.of(
        Arguments.of("{\"id\":null,\"mongoId\":null,\"invoiceNumber\":\"1\",\"date\":\"2019-06-14\","
            + "\"seller\":{\"companyId\":1,\"name\":\"Seller\",\"taxIdentificationNumber\":\"VAT_23\",\"address\":"
            + "{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\","
            + "\"countryCode\":\"PL\"}},\"buyer\":{\"companyId\":1,\"name\":\"Buyer\","
            + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":{\"addressId\":1,\"streetAddress\":"
            + "\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},"
            + "\"invoiceEntries\":[]}", new Invoice("1", LocalDate.of(2019, 6, 14),
            new Company(1L, "Seller", "VAT_23", InvoiceTestUtil.sampleAddress1()),
            new Company(1L, "Buyer", "VAT_23", InvoiceTestUtil.sampleAddress1()))),
        Arguments.of("{\"id\":null,\"mongoId\":null,\"invoiceNumber\":null,\"date\":\"2019-06-14\",\"seller\":"
            + "{\"companyId\":1,\"name\":\"Seller\",\"taxIdentificationNumber\":\"VAT_23\",\"address\":"
            + "{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\","
            + "\"countryCode\":\"PL\"}},\"buyer\":{\"companyId\":1,\"name\":\"Buyer\","
            + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":{\"addressId\":1,\"streetAddress\":"
            + "\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},"
            + "\"invoiceEntries\":[]}", new Invoice(null, LocalDate.of(2019, 6, 14),
            new Company(1L, "Seller", "VAT_23", InvoiceTestUtil.sampleAddress1()),
            new Company(1L, "Buyer", "VAT_23", InvoiceTestUtil.sampleAddress1()))),
        Arguments.of("{\"id\":null,\"mongoId\":null,\"invoiceNumber\":\"1\",\"date\":null,\"seller\":{\"companyId\":1,"
            + "\"name\":\"Seller\",\"taxIdentificationNumber\":\"VAT_23\",\"address\":{\"addressId\":1,"
            + "\"streetAddress\":\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\",\"countryCode\":"
            + "\"PL\"}},\"buyer\":{\"companyId\":1,\"name\":\"Buyer\",\"taxIdentificationNumber\":\"VAT_23\","
            + "\"address\":{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\",\"postalCode\":"
            + "\"00-123\",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},"
            + "\"invoiceEntries\":[]}", new Invoice("1", null,
            new Company(1L, "Seller", "VAT_23", InvoiceTestUtil.sampleAddress1()),
            new Company(1L, "Buyer", "VAT_23", InvoiceTestUtil.sampleAddress1()))),
        Arguments.of("{\"id\":null,\"mongoId\":null,\"invoiceNumber\":\"1\",\"date\":\"2019-06-14\",\"seller\":null,"
                + "\"buyer\":{\"companyId\":1,\"name\":\"Buyer\",\"taxIdentificationNumber\":\"VAT_23\","
                + "\"address\":{\"addressId\":1,\"streetAddress\":"
                + "\"Korkowa 2/12\",\"postalCode\":\"00-123\",\"city\":\"Warszawa\","
                + "\"countryCode\":\"PL\"}},\"invoiceEntries\":[]}",
            new Invoice("1", LocalDate.of(2019, 6, 14), null,
                new Company(1L, "Buyer", "VAT_23", InvoiceTestUtil.sampleAddress1()))),
        Arguments.of("{\"id\":null,\"mongoId\":null,\"invoiceNumber\":\"1\",\"date\":\"2019-06-14\","
                + "\"seller\":{\"companyId\":1,\"name\":\"Seller\",\"taxIdentificationNumber\":\"VAT_23\","
                + "\"address\":{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\",\"postalCode\":\"00-123\","
                + "\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},\"buyer\":null,\"invoiceEntries\":[]}",
            new Invoice("1", LocalDate.of(2019, 6, 14),
                new Company(1L, "Seller", "VAT_23", InvoiceTestUtil.sampleAddress1()),
                null))
    );
  }

  @Test
  @DisplayName("Should return proper invoice object when invoice elements was added")
  void shouldReturnProperInvoiceObjectWhenInvoiceElementsWasAdded() throws IOException {
    // Given
    String given = "{\"id\":null,\"mongoId\":null,\"invoiceNumber\":\"1\",\"date\":\"2019-06-14\","
        + "\"seller\":{\"companyId\":1,\"name\":\"Seller\","
        + "\"taxIdentificationNumber\":\"VAT_23\",\"address\":{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\","
        + "\"postalCode\":\"00-123\",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},"
        + "\"buyer\":{\"companyId\":1,\"name\":\"Buyer\",\"taxIdentificationNumber\":\"VAT_23\","
        + "\"address\":{\"addressId\":1,\"streetAddress\":\"Korkowa 2/12\",\"postalCode\""
        + ":\"00-123\",\"city\":\"Warszawa\",\"countryCode\":\"PL\"}},\"invoiceEntries\":"
        + "[{\"invoiceEntryId\":1,\"description\":\"Mąka\",\"quantity\":1,\"value\":10.4000,\"vatRate\":\"VAT_23\","
        + "\"vatValue\":2.3920},{\"invoiceEntryId\":2,\"description\":\"Cukier\",\"quantity\":2,\"value\":3.5000,"
        + "\"vatRate\":\"VAT_23\",\"vatValue\":0.8050},{\"invoiceEntryId\":3,\"description\":\"Sól\",\"quantity\":3,"
        + "\"value\":5.6400,\"vatRate\":\"VAT_23\",\"vatValue\":1.2972}]}";
    Invoice expected = new Invoice("1", LocalDate.of(2019, 6, 14),
        new Company(1L, "Seller", "VAT_23", InvoiceTestUtil.sampleAddress1()),
        new Company(1L, "Buyer", "VAT_23", InvoiceTestUtil.sampleAddress1()));
    expected.addInvoiceEntry(1L, "Mąka", 1, BigDecimal.valueOf(10.4), Vat.VAT_23);
    expected.addInvoiceEntry(2L, "Cukier", 2, BigDecimal.valueOf(3.5), Vat.VAT_23);
    expected.addInvoiceEntry(3L, "Sól", 3, BigDecimal.valueOf(5.64), Vat.VAT_23);

    // When
    InvoiceJsonConverter invoiceJsonConverter = new InvoiceJsonConverter();
    Invoice result = invoiceJsonConverter.fromJson(given);

    // Then
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Should throw exception when passed parameter JSON is null")
  void shouldThrowExceptionWhenPassedParameterJsonIsNull() {
    InvoiceJsonConverter invoiceJsonConverter = new InvoiceJsonConverter();
    assertThrows(IllegalArgumentException.class, () -> invoiceJsonConverter.fromJson(null));
  }

  @Test
  @DisplayName("Should throw exception when passed parameter JSON is empty")
  void shouldThrowExceptionWhenPassedParameterJsonIsEmpty() {
    InvoiceJsonConverter invoiceJsonConverter = new InvoiceJsonConverter();
    assertThrows(IllegalArgumentException.class, () -> invoiceJsonConverter.fromJson(""));
  }
}
