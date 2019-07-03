package pl.coderstrust.database;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.Vat;

public class Main {

  public static void main(String[] args) throws IOException {

    Company company1 = new Company("XYZ", "11111", "London");
    Company company2 = new Company("QAZ", "22222", "Chicago");
    Company company3 = new Company("PKL", "33333", "Warsaw");

    Invoice invoice1 = new Invoice("1", LocalDate.parse("2019-01-01"), company1, company2);
    Invoice invoice2 = new Invoice("2", LocalDate.parse("2019-02-01"), company3, company2);

    invoice1.addInvoiceEntry("Kiwi", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice1.addInvoiceEntry("Jablka", 100, BigDecimal.valueOf(1.99), Vat.VAT_8);
    invoice1.addInvoiceEntry("Pomarancze", 254, BigDecimal.valueOf(99.00), Vat.VAT_0);

    invoice2.addInvoiceEntry("Arbuz", 10, BigDecimal.valueOf(125.23), Vat.VAT_23);
    invoice2.addInvoiceEntry("Jagoda", 1000, BigDecimal.valueOf(1.99), Vat.VAT_8);

  /*  InFileDatabase inFileDatabase = new InFileDatabase();
    inFileDatabase.saveInvoice(invoice1);
    inFileDatabase.saveInvoice(invoice2);*/



  }
}
