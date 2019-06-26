package pl.coderstrust.model;

import java.math.BigDecimal;
import java.util.Objects;

public class InvoiceEntry {

  private String description;
  private int quantity;
  private BigDecimal value;
  private Vat vatRate;
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
