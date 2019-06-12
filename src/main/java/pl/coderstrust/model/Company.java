package pl.coderstrust.model;

import java.util.Objects;

public class Company {

  private String name;
  private String taxIdentificationNumber;
  private String address;

  public Company(String name, String taxIdentificationNumber, String address) {
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
