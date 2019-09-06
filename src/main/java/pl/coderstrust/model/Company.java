package pl.coderstrust.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@ApiModel(value = "Company", description = "Company")
@Entity
@Table(name = "company")
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq_generator")
  @SequenceGenerator(name = "company_seq_generator", sequenceName = "company_seq")
  @ApiModelProperty(value = "identifier of company", example = "null", dataType = "Long")
  @Column(name = "company_id")
  private Long companyId;
  @ApiModelProperty(value = "Name of customer", example = "ABC Company", dataType = "String")
  @Column(name = "company_name")
  private String name;
  @ApiModelProperty(value = "Tax identification number", example = "123-456-78-90", dataType = "String")
  @Column(name = "company_tax_identification_number")
  private String taxIdentificationNumber;
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "company_address_id")
  private Address address;

  private Company() {
  }

  public Company(Long companyId, String name, String taxIdentificationNumber, Address address) {
    this.companyId = companyId;
    this.name = name;
    this.taxIdentificationNumber = taxIdentificationNumber;
    this.address = address;
  }

  public Long getCompanyId() {
    return companyId;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Company company = (Company) o;
    return Objects.equals(companyId, company.companyId)
        && Objects.equals(name, company.name)
        && Objects.equals(taxIdentificationNumber, company.taxIdentificationNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(companyId, name, taxIdentificationNumber);
  }
}
