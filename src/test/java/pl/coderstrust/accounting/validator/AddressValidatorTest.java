package pl.coderstrust.accounting.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.coderstrust.model.Address;

class AddressValidatorTest {

  private static final Validator<Address> validator = new AddressValidator();

  @Test
  @DisplayName("Should validate correct Address")
  void shouldValidateCorrectAddress() {
    Address address = new Address("ul. Jakakolwiek 34/5A", "31-021", "Kraków", "PL");
    List<String> result = validator.validate(address);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should validate address with all nulls")
  void shouldValidateAddressWithAllNulls() {
    Address address = new Address(null, null, null, null);
    List<String> result = validator.validate(address);
    assertEquals(4, result.size());
    result.forEach(message -> assertTrue(message.contains("null")));
  }

  @Test
  @DisplayName("Should validate address with all empty parameters")
  void shouldValidateAddressWithAllEmptyStrings() {
    Address address = new Address("", "", "", "");
    List<String> result = validator.validate(address);
    assertEquals(4, result.size());
    result.forEach(message -> assertTrue(message.contains("empty")));
  }

  @Test
  @DisplayName("Should validate incorrect country code")
  void shouldValidateIncorrectCountryCode() {
    Address address = new Address("ul. Jakakolwiek 34/5A", "31-021", "Kraków", "Poland");
    List<String> result = validator.validate(address);
    assertEquals(1, result.size());
    assertTrue(result.get(0).toLowerCase().contains("country"));
  }

  @Test
  @DisplayName("Should validate incorrect postal code")
  void shouldValidateIncorrectPostalCode() {
    Address address = new Address("ul. Jakakolwiek 34/5A", "31021", "Kraków", "DE");
    List<String> result = validator.validate(address);
    assertEquals(1, result.size());
    assertTrue(result.get(0).toLowerCase().contains("postal code"));
  }

}