package pl.coderstrust.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Company {

  private String name;
  private String taxIdentificationNumber;
  private String address;

  @JsonCreator
  public Company(@JsonProperty("name") String name,
      @JsonProperty("taxIdentificationNumber") String taxIdentificationNumber,
      @JsonProperty("address") String address) {
    this.name = name;
    this.taxIdentificationNumber = taxIdentificationNumber;
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTaxIdentificationNumber() {
    return taxIdentificationNumber;
  }

  public void setTaxIdentificationNumber(String taxIdentificationNumber) {
    this.taxIdentificationNumber = taxIdentificationNumber;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Company company = (Company) o;
    return Objects.equals(name, company.name)
        && Objects.equals(taxIdentificationNumber, company.taxIdentificationNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, taxIdentificationNumber);
  }
}
