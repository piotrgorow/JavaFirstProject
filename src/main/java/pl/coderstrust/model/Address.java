package pl.coderstrust.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@ApiModel(value = "Address", description = "Address")
@Entity
@Table(name = "address")
public class Address {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq_generator")
  @SequenceGenerator(name = "address_seq_generator", sequenceName = "address_seq")
  @ApiModelProperty(value = "identifier of address", example = "null", dataType = "Long")
  @Column(name = "address_id")
  private Long addressId;
  @ApiModelProperty(value = "Street address", example = "Baker Street 1/2", dataType = "String")
  @Column(name = "address_street")
  private String streetAddress;
  @ApiModelProperty(value = "Postal code", example = "12-345", dataType = "String")
  @Column(name = "address_postal_code")
  private String postalCode;
  @ApiModelProperty(value = "City", example = "London", dataType = "String")
  @Column(name = "address_city")
  private String city;
  @ApiModelProperty(value = "Country code", example = "UK", dataType = "String")
  @Column(name = "address_country_code")
  private String countryCode;

  private Address() {
    // Constructor for serialization purposes only
  }

  public Address(Long addressId, String streetAddress, String postalCode, String city) {
    this.addressId = addressId;
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
    countryCode = "PL";
  }

  public Address(Long addressId, String streetAddress, String postalCode, String city, String countryCode) {
    this.addressId = addressId;
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
    this.countryCode = countryCode;
  }

  public Long getAddressId() {
    return addressId;
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
    return Objects.equals(addressId, address.addressId)
        && Objects.equals(streetAddress, address.streetAddress)
        && Objects.equals(postalCode, address.postalCode)
        && Objects.equals(city, address.city)
        && Objects.equals(countryCode, address.countryCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addressId, streetAddress, postalCode, city, countryCode);
  }
}
