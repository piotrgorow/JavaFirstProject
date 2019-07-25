package pl.coderstrust.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Objects;

@ApiModel(value = "InvoiceEntry", description = "InvoiceEntry")
public class InvoiceEntry {

  @ApiModelProperty(value = "Description", example = "Apple", dataType = "String")
  private String description;
  @ApiModelProperty(value = "Quantity", example = "2", dataType = "int")
  private int quantity;
  @ApiModelProperty(value = "Value", example = "10.25", dataType = "BigDecimal")
  private BigDecimal value;
  @ApiModelProperty(value = "Tax", example = "VAT_23", dataType = "Vat")
  private Vat vatRate;
  @ApiModelProperty(value = "Value of tax", example = "2.36", dataType = "BigDecimal")
  private BigDecimal vatValue;

  private InvoiceEntry() {
  }

  public InvoiceEntry(String description, int quantity, BigDecimal value, Vat vatRate) {
    this.description = description;
    this.quantity = quantity;
    this.value = value;
    this.vatRate = vatRate;
    vatValue = vatRate.getValue().multiply(value);
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
    return Objects.equals(description, that.description)
        && Objects.equals(vatValue, that.vatValue)
        && Objects.equals(value, that.value)
        && vatRate == that.vatRate;
  }

  @Override
  public int hashCode() {
    return Objects.hash(description, vatValue, value, vatRate);
  }
}
