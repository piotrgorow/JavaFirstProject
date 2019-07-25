package pl.coderstrust.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

@ApiModel(value = "Company", description = "Company")
public class Company {

  @ApiModelProperty(value = "Name of customer", example = "ABC Company", dataType = "String")
  private String name;
  @ApiModelProperty(value = "Tax identification number", example = "123-456-78-90", dataType = "String")
  private String taxIdentificationNumber;
  private Address address;

  private Company() {
  }

  public Company(String name, String taxIdentificationNumber, Address address) {
    this.name = name;
    this.taxIdentificationNumber = taxIdentificationNumber;
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public String getTaxIdentificationNumber() {
    return taxIdentificationNumber;
  }

  public void setTaxIdentificationNumber(String taxIdentificationNumber) {
    this.taxIdentificationNumber = taxIdentificationNumber;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
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
