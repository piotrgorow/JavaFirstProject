package pl.coderstrust.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Invoice {

  private String id;
  private LocalDate date;
  private Company seller;
  private Company buyer;
  private List<InvoiceEntry> invoiceEntries;

  public Invoice(String id, LocalDate date, Company seller, Company buyer) {
    this.id = id;
    this.date = date;
    this.seller = seller;
    this.buyer = buyer;
    invoiceEntries = new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public Company getSeller() {
    return seller;
  }

  public void setSeller(Company seller) {
    this.seller = seller;
  }

  public Company getBuyer() {
    return buyer;
  }

  public void setBuyer(Company buyer) {
    this.buyer = buyer;
  }

  public List<InvoiceEntry> getInvoiceEntries() {
    return invoiceEntries;
  }

  public void addInvoiceEntry(String description, int quantity, BigDecimal value, Vat vatRate) {
    invoiceEntries.add(new InvoiceEntry(description, quantity, value, vatRate));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Invoice invoice = (Invoice) o;
    return Objects.equals(id, invoice.id)
        && Objects.equals(date, invoice.date)
        && Objects.equals(seller, invoice.seller)
        && Objects.equals(buyer, invoice.buyer)
        && Objects.equals(invoiceEntries, invoice.invoiceEntries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, date, seller, buyer, invoiceEntries);
  }
}
