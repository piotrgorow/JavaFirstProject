package pl.coderstrust.database;

import java.math.BigDecimal;
import java.time.LocalDate;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.Vat;

class InvoiceTestUtil {

  static String sampleInvoiceJson = "{\"id\":1,\"invoiceNumber\":\"inv1\",\"date\":\"2019-01-01\","
      + "\"seller\":{\"name\":\"XYZ\",\"taxIdentificationNumber\":\"11111\","
      + "\"address\":\"London\"},\"buyer\":{\"name\":\"QAZ\",\"taxIdentificationNumber\":\"22222\","
      + "\"address\":\"Chicago\"},\"invoiceEntries\":[{\"description\":\"Kiwi\",\"quantity\":10,"
      + "\"value\":125.23,\"vatRate\":\"VAT_23\",\"vatValue\":28.8029},{\"description\":\"Jablka\","
      + "\"quantity\":100,\"value\":1.99,\"vatRate\":\"VAT_8\",\"vatValue\":0.1592},"
      + "{\"description\":\"Pomarancze\",\"quantity\":254,\"value\":99.0,\"vatRate\":\"VAT_0\","
      + "\"vatValue\":0.00}]}";
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
    invoice1.addInvoiceEntry("Kiwi", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice1.addInvoiceEntry("Jablka", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice1.addInvoiceEntry("Pomarancze", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    return invoice1;
  }

  static Invoice sampleInvoiceFromFile() {
    Invoice invoice1 = new Invoice("inv1", LocalDate.parse("2019-01-01"),
        InvoiceTestUtil.sampleCompany1(), InvoiceTestUtil.sampleCompany2());
    invoice1.addInvoiceEntry("Kiwi", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice1.addInvoiceEntry("Jablka", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice1.addInvoiceEntry("Pomarancze", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    invoice1.setId(1L);
    return invoice1;
  }

  static Invoice sampleInvoice2() {
    Invoice invoice2 = new Invoice("inv2", LocalDate.parse("2019-02-01"),
        InvoiceTestUtil.sampleCompany2(), InvoiceTestUtil.sampleCompany3());
    invoice2.addInvoiceEntry("Arbuz", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice2.addInvoiceEntry("Jagoda", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice2.addInvoiceEntry("Winogrona", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    return invoice2;
  }

  static Invoice sampleInvoiceFromFile2() {
    Invoice invoice2 = new Invoice("inv2", LocalDate.parse("2019-02-01"),
        InvoiceTestUtil.sampleCompany2(), InvoiceTestUtil.sampleCompany3());
    invoice2.addInvoiceEntry("Arbuz", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice2.addInvoiceEntry("Jagoda", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice2.addInvoiceEntry("Winogrona", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    invoice2.setId(2L);
    return invoice2;
  }

  static Invoice sampleInvoice3() {
    Invoice invoice2 = new Invoice("inv3", LocalDate.parse("2019-03-01"),
        InvoiceTestUtil.sampleCompany3(), InvoiceTestUtil.sampleCompany1());
    invoice2.addInvoiceEntry("Papaya", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice2.addInvoiceEntry("Lemon", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice2.addInvoiceEntry("Oregano", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    return invoice2;
  }

  static Invoice sampleInvoiceFromFile3() {
    Invoice invoice3 = new Invoice("inv3", LocalDate.parse("2019-03-01"),
        InvoiceTestUtil.sampleCompany3(), InvoiceTestUtil.sampleCompany1());
    invoice3.addInvoiceEntry("Papaya", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice3.addInvoiceEntry("Lemon", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice3.addInvoiceEntry("Oregano", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    invoice3.setId(3L);
    return invoice3;
  }

  private static Company sampleCompany1() {
    return new Company("XYZ", "11111", "London");
  }

  private static Company sampleCompany2() {
    return new Company("QAZ", "22222", "Chicago");
  }

  private static Company sampleCompany3() {
    return new Company("PKL", "33333", "Warsaw");
  }
}
