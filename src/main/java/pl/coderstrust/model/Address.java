package pl.coderstrust.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;

@ApiModel(value = "Address", description = "Address")
public class Address {

  @ApiModelProperty(value = "Street address", example = "Baker Street 1/2", dataType = "String")
  private String streetAddress;
  @ApiModelProperty(value = "Postal code", example = "12-345", dataType = "String")
  private String postalCode;
  @ApiModelProperty(value = "City", example = "London", dataType = "String")
  private String city;
  @ApiModelProperty(value = "Country code", example = "UK", dataType = "String")
  private String countryCode;

  private Address() {
    // Constructor for serialization purposes only
  }

  public Address(String streetAddress, String postalCode, String city) {
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
    countryCode = "PL";
  }

  public Address(String streetAddress, String postalCode, String city, String countryCode) {
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
    this.countryCode = countryCode;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getCity() {
    return city;
  }

  public String getCountryCode() {
    return countryCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Address address = (Address) o;
    return Objects.equals(streetAddress, address.streetAddress)
        && Objects.equals(postalCode, address.postalCode)
        && Objects.equals(city, address.city)
        && Objects.equals(countryCode, address.countryCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(streetAddress, postalCode, city, countryCode);
  }
}
