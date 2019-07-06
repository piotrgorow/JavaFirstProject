package pl.coderstrust.accounting.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.coderstrust.database.InvoiceTestUtil;
import pl.coderstrust.model.Company;

class CompanyValidatorTest {

  private static final Validator<Company> validator = new CompanyValidator();

  @Test
  @DisplayName("Should validate correct company")
  void shouldValidateCorrectCompany() {
    // When
    Company company = new Company("Firma1", "999-888-77-66", InvoiceTestUtil.sampleAddress1());
    List<String> result = CompanyValidatorTest.validator.validate(company);

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should validate company with null name")
  void shouldValidateCompanyWithNullName() {
    // When
    Company company = new Company(null, "999-888-77-66", InvoiceTestUtil.sampleAddress1());
    String expected = "Company name must not be null or empty";
    List<String> result = CompanyValidatorTest.validator.validate(company);

    // Then
    assertEquals(expected, result.get(0));
  }

  @Test
  @DisplayName("Should validate company with empty name")
  void shouldValidateCompanyWithEmptyName() {
    // When
    Company company = new Company("", "999-888-77-66", InvoiceTestUtil.sampleAddress1());
    String expected = "Company name must not be null or empty";
    List<String> result = CompanyValidatorTest.validator.validate(company);

    // Then
    assertEquals(expected, result.get(0));
  }

  @Test
  @DisplayName("Should validate company with null tax ID")
  void shouldValidateCompanyWithNullTaxID() {
    // When
    Company company = new Company("Firma1", null, InvoiceTestUtil.sampleAddress1());
    String expected = "Company tax ID must not be null or empty";
    List<String> result = CompanyValidatorTest.validator.validate(company);

    // Then
    assertEquals(expected, result.get(0));
  }

  @Test
  @DisplayName("Should validate company with empty tax ID")
  void shouldValidateCompanyWithEmptyTaxID() {
    // When
    Company company = new Company("Firma1", "", InvoiceTestUtil.sampleAddress1());
    String expected = "Company tax ID must not be null or empty";
    List<String> result = CompanyValidatorTest.validator.validate(company);

    // Then
    assertEquals(expected, result.get(0));
  }

  @Test
  @DisplayName("Should validate company with incorrect tax ID")
  void shouldValidateCompanyWithIncorrectTaxId() {
    // When
    Company company = new Company("Firma1", "999-89-89-77", InvoiceTestUtil.sampleAddress1());
    String expected = "Company tax ID must be in the format XXX-XXX-XX-XX";
    List<String> result = CompanyValidatorTest.validator.validate(company);

    // Then
    assertEquals(expected, result.get(0));
  }
}
