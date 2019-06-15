package pl.coderstrust.database;

import java.math.BigDecimal;
import java.time.LocalDate;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.Vat;

class InvoiceTestUtil {

  static Invoice sampleInvoice() {
    Invoice invoice1 = new Invoice("inv1", LocalDate.parse("2019-01-01"),
        InvoiceTestUtil.sampleCompany1(), InvoiceTestUtil.sampleCompany2());
    invoice1.addInvoiceEntry("Kiwi", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice1.addInvoiceEntry("Jablka", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice1.addInvoiceEntry("Pomarancze", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);
    return invoice1;
  }

  static Invoice sampleInvoice2() {
    Invoice invoice2 = new Invoice("inv2", LocalDate.parse("2019-02-01"),
        InvoiceTestUtil.sampleCompany2(), InvoiceTestUtil.sampleCompany3());
    invoice2.addInvoiceEntry("Arbuz", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice2.addInvoiceEntry("Jagoda", 1000, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice2.addInvoiceEntry("Winogrona", 1, BigDecimal.valueOf(99.00), Vat.VAT_0);
    return invoice2;
  }

  static Invoice sampleInvoice3() {
    Invoice invoice2 = new Invoice("inv3", LocalDate.parse("2019-03-01"),
        InvoiceTestUtil.sampleCompany3(), InvoiceTestUtil.sampleCompany1());
    invoice2.addInvoiceEntry("Papaya", 120, BigDecimal.valueOf(15.23), Vat.VAT_8);
    invoice2.addInvoiceEntry("Lemon", 11, BigDecimal.valueOf(11.99), Vat.VAT_0);
    invoice2.addInvoiceEntry("Oregano", 15, BigDecimal.valueOf(99.99), Vat.VAT_0);
    return invoice2;
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