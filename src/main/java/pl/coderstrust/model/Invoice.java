package pl.coderstrust.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@ApiModel(value = "Invoice", description = "Invoice")
@Entity
@Table(name = "invoice")
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_seq_generator")
  @SequenceGenerator(name = "invoice_seq_generator", sequenceName = "invoice_seq")
  @ApiModelProperty(value = "identifier of invoice", example = "1", dataType = "Long")
  @Column(name = "invoice_id")
  private Long id;
  @ApiModelProperty(value = "identifier of invoice", example = "1", dataType = "String")
  @Column(name = "mongo_id")
  @org.springframework.data.annotation.Id
  private String mongoId;
  @ApiModelProperty(value = "Invoice number", example = "INV 1/01/2019", dataType = "String")
  @Column(name = "invoice_number")
  private String invoiceNumber;
  @ApiModelProperty(value = "Date of invoice", example = "2019-01-01", dataType = "LocalDate")
  @Column(name = "invoice_date")
  private LocalDate date;
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "invoice_seller_id")
  private Company seller;
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "invoice_buyer_id")
  private Company buyer;
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "invoice_id")
  @Column(name = "invoice_entries")
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

  public void addInvoiceEntry(Long invoiceEntryId, String description, int quantity, BigDecimal value, Vat vatRate) {
    invoiceEntries.add(new InvoiceEntry(invoiceEntryId, description, quantity, value, vatRate));
  }

  public Long getId() {
    return id;
  }

  public String getMongoId() {
    return mongoId;
  }

  public void setId(Long id) {
    this.id = id;
    mongoId = String.valueOf(id);
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
        && Objects.equals(mongoId, invoice.mongoId)
        && Objects.equals(invoiceNumber, invoice.invoiceNumber)
        && Objects.equals(date, invoice.date)
        && Objects.equals(seller, invoice.seller)
        && Objects.equals(buyer, invoice.buyer)
        && Objects.equals(invoiceEntries, invoice.invoiceEntries);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, mongoId, invoiceNumber, date, seller, buyer, invoiceEntries);
  }
}
