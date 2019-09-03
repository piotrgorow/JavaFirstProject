package pl.coderstrust.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@ApiModel(value = "InvoiceEntry", description = "InvoiceEntry")
@Entity
@Table(name = "invoice_entry")
public class InvoiceEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoice_entry_seq_generator")
  @SequenceGenerator(name = "invoice_entry_seq_generator", sequenceName = "invoice_entry_seq")
  @ApiModelProperty(value = "identifier of invoice entry", example = "null", dataType = "Long")
  @Column(name = "invoice_entry_id")
  private Long invoiceEntryId;
  @ApiModelProperty(value = "Description", example = "Apple", dataType = "String")
  @Column(name = "invoice_entry_description")
  private String description;
  @ApiModelProperty(value = "Quantity", example = "2", dataType = "int")
  @Column(name = "invoice_entry_quantity")
  private int quantity;
  @ApiModelProperty(value = "Value", example = "10.25", dataType = "BigDecimal")
  @Column(name = "invoice_entry_value")
  private BigDecimal value;
  @ApiModelProperty(value = "Tax", example = "VAT_23", dataType = "Vat")
  @Enumerated(EnumType.STRING)
  @Column(name = "invoice_entry_vat_rate")
  private Vat vatRate;
  @ApiModelProperty(value = "Value of tax", example = "2.36", dataType = "BigDecimal")
  @Column(name = "invoice_entry_vat_value")
  private BigDecimal vatValue;

  private InvoiceEntry() {
  }

  public InvoiceEntry(Long invoiceEntryId, String description, int quantity, BigDecimal value, Vat vatRate) {
    this.invoiceEntryId = invoiceEntryId;
    this.description = description;
    this.quantity = quantity;
    this.value = value;
    this.vatRate = vatRate;
    vatValue = vatRate.getValue().multiply(value);
  }

  public Long getInvoiceEntryId() {
    return invoiceEntryId;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getValue() {
    return value;
  }

  public Vat getVatRate() {
    return vatRate;
  }

  public BigDecimal getVatValue() {
    return vatValue;
  }

  public int getQuantity() {
    return quantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceEntry that = (InvoiceEntry) o;
    return Objects.equals(invoiceEntryId, that.invoiceEntryId)
        && Objects.equals(description, that.description)
        && Objects.equals(vatValue, that.vatValue)
        && Objects.equals(value, that.value)
        && vatRate == that.vatRate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(invoiceEntryId, description, vatValue, value, vatRate);
  }
}
