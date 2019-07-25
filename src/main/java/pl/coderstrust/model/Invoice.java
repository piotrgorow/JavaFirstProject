package pl.coderstrust.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiModel(value = "Invoice", description = "Invoice")
public class Invoice {

  @ApiModelProperty(value = "identifier of invoice", example = "1", dataType = "Long")
  private Long id;
  @ApiModelProperty(value = "Invoice number", example = "INV 1/01/2019", dataType = "String")
  private String invoiceNumber;
  @ApiModelProperty(value = "Date of invoice", example = "2019-01-01", dataType = "LocalDate")
  private LocalDate date;
  private Company seller;
  private Company buyer;
  private List<InvoiceEntry> invoiceEntries;

  private Invoice() {
  }

  public Invoice(String invoiceNumber, LocalDate date, Company seller, Company buyer) {
    this.invoiceNumber = invoiceNumber;
    this.date = date;
    this.seller = seller;
    this.buyer = buyer;
    invoiceEntries = new ArrayList<>();
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public LocalDate getDate() {
    return date;
  }

  public Company getSeller() {
    return seller;
  }

  public Company getBuyer() {
    return buyer;
  }

  public List<InvoiceEntry> getInvoiceEntries() {
    return invoiceEntries;
  }

  public void addInvoiceEntry(String description, int quantity, BigDecimal value, Vat vatRate) {
    invoiceEntries.add(new InvoiceEntry(description, quantity, value, vatRate));
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
        && Objects.equals(invoiceNumber, invoice.invoiceNumber)
        && Objects.equals(date, invoice.date)
        && Objects.equals(seller, invoice.seller)
        && Objects.equals(buyer, invoice.buyer)
        && Objects.equals(invoiceEntries, invoice.invoiceEntries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, invoiceNumber, date, seller, buyer,
        invoiceEntries);
  }
}
