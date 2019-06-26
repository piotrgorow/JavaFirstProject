package pl.coderstrust.model;

import java.util.Objects;

public class Address {

  private String streetAddress;
  private String postalCode;
  private String city;
  private String countryCode;

  private Address() {
    // Constructor for serialization purposes only
  }

  public Address(String streetAddress, String postalCode, String city) {
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
    this.countryCode = "PL";
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
    return Objects.equals(streetAddress, address.streetAddress) &&
        Objects.equals(postalCode, address.postalCode) &&
        Objects.equals(city, address.city) &&
        Objects.equals(countryCode, address.countryCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(streetAddress, postalCode, city, countryCode);
  }
}
