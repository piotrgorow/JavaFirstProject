package pl.coderstrust.database;

import java.math.BigDecimal;
import java.time.LocalDate;
import pl.coderstrust.model.Address;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.Vat;

public class InvoiceTestUtil {

  static String sampleInvoiceJson = "{\"id\":1,\"invoiceNumber\":\"inv1\",\"date\":\"2019-01-01\","
      + "\"seller\":{\"name\":\"XYZ\",\"taxIdentificationNumber\":\"11111\","
      + "\"address\":\"London\"},\"buyer\":{\"name\":\"QAZ\",\"taxIdentificationNumber\":\"22222\","
      + "\"address\":\"Chicago\"},\"invoiceEntries\":[{\"description\":\"Kiwi\",\"quantity\":10,"
      + "\"value\":125.23,\"vatRate\":\"VAT_23\",\"vatValue\":28.8029},{\"description\":\"Jablka\",\"quantity\":100,"
      + "\"value\":1.99,\"vatRate\":\"VAT_8\",\"vatValue\":0.1592},{\"description\":\"Pomarancze\",\"quantity\":254,"
      + "\"value\":99.0,\"vatRate\":\"VAT_0\",\"vatValue\":0.00}]}";
  static String sampleInvoiceJson2 = "{\"id\":2,\"invoiceNumber\":\"inv2\",\"date\":\"2019-02-01\","
      + "\"seller\":{\"name\":\"QAZ\",\"taxIdentificationNumber\":\"22222\",\"address\":"
      + "\"Chicago\"},\"buyer\":{\"name\":\"PKL\",\"taxIdentificationNumber\":\"33333\","
      + "\"address\":\"Warsaw\"},\"invoiceEntries\":[{\"description\":\"Arbuz\",\"quantity\":10,"
      + "\"value\":125.23,\"vatRate\":\"VAT_23\",\"vatValue\":28.8029},{\"description\":\"Jagoda\","
      + "\"quantity\":100,\"value\":1.99,\"vatRate\":\"VAT_8\",\"vatValue\":0.1592},"
      + "{\"description\":\"Winogrona\",\"quantity\":254,\"value\":99.0,\"vatRate\":\"VAT_0\","
      + "\"vatValue\":0.00}]}";
  static String sampleInvoiceJson3 = "{\"id\":3,\"invoiceNumber\":\"inv3\",\"date\":\"2019-03-01\","
      + "\"seller\":{\"name\":\"PKL\",\"taxIdentificationNumber\":\"33333\",\"address\":"
      + "\"Warsaw\"},\"buyer\":{\"name\":\"XYZ\",\"taxIdentificationNumber\":\"11111\","
      + "\"address\":\"London\"},\"invoiceEntries\":[{\"description\":\"Papaya\",\"quantity\":10,"
      + "\"value\":125.23,\"vatRate\":\"VAT_23\",\"vatValue\":28.8029},{\"description\":\"Lemon\","
      + "\"quantity\":100,\"value\":1.99,\"vatRate\":\"VAT_8\",\"vatValue\":0.1592},"
      + "{\"description\":\"Oregano\",\"quantity\":254,\"value\":99.0,\"vatRate\":\"VAT_0\","
      + "\"vatValue\":0.00}]}";

  static Invoice sampleInvoice() {
    Invoice invoice1 = new Invoice("inv1", LocalDate.parse("2019-01-01"),
        InvoiceTestUtil.sampleCompany1(), InvoiceTestUtil.sampleCompany2());
    invoice1.addInvoiceEntry(1L, "Kiwi", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice1.addInvoiceEntry(2L, "Jablka", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice1.addInvoiceEntry(3L, "Pomarancze", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    return invoice1;
  }

  public static Invoice sampleInvoiceFromFile() {
    Invoice invoice1 = new Invoice("inv1", LocalDate.parse("2019-01-01"),
        InvoiceTestUtil.sampleCompany1(), InvoiceTestUtil.sampleCompany2());
    invoice1.addInvoiceEntry(1L, "Kiwi", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice1.addInvoiceEntry(2L, "Jablka", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice1.addInvoiceEntry(3L, "Pomarancze", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    invoice1.setId(1L);
    return invoice1;
  }

  static Invoice sampleInvoice2() {
    Invoice invoice2 = new Invoice("inv2", LocalDate.parse("2019-02-01"),
        InvoiceTestUtil.sampleCompany2(), InvoiceTestUtil.sampleCompany3());
    invoice2.addInvoiceEntry(1L, "Arbuz", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice2.addInvoiceEntry(2L, "Jagoda", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice2.addInvoiceEntry(3L, "Winogrona", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    return invoice2;
  }

  public static Invoice sampleInvoiceFromFile2() {
    Invoice invoice2 = new Invoice("inv2", LocalDate.parse("2019-02-01"),
        InvoiceTestUtil.sampleCompany2(), InvoiceTestUtil.sampleCompany3());
    invoice2.addInvoiceEntry(1L, "Arbuz", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice2.addInvoiceEntry(2L, "Jagoda", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice2.addInvoiceEntry(3L, "Winogrona", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    invoice2.setId(2L);
    return invoice2;
  }

  static Invoice sampleInvoice3() {
    Invoice invoice2 = new Invoice("inv3", LocalDate.parse("2019-03-01"),
        InvoiceTestUtil.sampleCompany3(), InvoiceTestUtil.sampleCompany1());
    invoice2.addInvoiceEntry(1L, "Papaya", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice2.addInvoiceEntry(2L, "Lemon", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice2.addInvoiceEntry(3L, "Oregano", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    return invoice2;
  }

  public static Invoice sampleInvoiceFromFile3() {
    Invoice invoice3 = new Invoice("inv3", LocalDate.parse("2019-03-01"),
        InvoiceTestUtil.sampleCompany3(), InvoiceTestUtil.sampleCompany1());
    invoice3.addInvoiceEntry(1L, "Papaya", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice3.addInvoiceEntry(2L, "Lemon", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice3.addInvoiceEntry(3L, "Oregano", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    invoice3.setId(3L);
    return invoice3;
  }

  private static Company sampleCompany1() {
    return new Company(1L, "XYZ", "11111", sampleAddress1());
  }

  private static Company sampleCompany2() {
    return new Company(1L, "QAZ", "22222", sampleAddress1());
  }

  private static Company sampleCompany3() {
    return new Company(1L, "PKL", "33333", sampleAddress2());
  }

  public static Address sampleAddress1() {
    return new Address(1L, "Korkowa 2/12", "00-123", "Warszawa");
  }

  private static Address sampleAddress2() {
    return new Address(1L, "Testowa 15A", "31-123", "Kielce", "DE");
  }
}
