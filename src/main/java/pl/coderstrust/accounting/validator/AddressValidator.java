package pl.coderstrust.accounting.validator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import pl.coderstrust.accounting.Utils;
import pl.coderstrust.model.Address;

@Slf4j
public class AddressValidator implements Validator<Address> {

  private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("\\d{2}-\\d{3}");

  @Override
  public List<String> validate(Address address) {
    List<String> result = new LinkedList<>();
    if (Utils.isNullOrEmpty(address.getCity())) {
      log.warn("Parameter city cannot be null or empty");
      result.add("City must not be null or empty");
    }
    if (Utils.isNullOrEmpty(address.getStreetAddress())) {
      log.warn("Parameter streetAddress cannot be null or empty");
      result.add("Street address must not be null or empty");
    }
    if (Utils.isNullOrEmpty(address.getCountryCode())) {
      log.warn("Parameter countryCode cannot be null or empty");
      result.add("Country must not be null or empty");
    } else {
      if (Arrays.stream(Locale.getISOCountries())
          .noneMatch(s -> s.equals(address.getCountryCode()))) {
        log.warn("Parameter countryCode must be in the IDO format");
        result.add("Country must be in the ISO format");
      }
    }
    if (Utils.isNullOrEmpty(address.getPostalCode())) {
      log.warn("Parameter postalCode cannot be null or empty");
      result.add("Postal code must not be null or empty");
    } else {
      if (!POSTAL_CODE_PATTERN.matcher(address.getPostalCode()).matches()) {
        log.warn("Parameter postalCode must be in the format XX-XXX");
        result.add("Postal code must be in the format XX-XXX");
      }
    }
    return result;
  }
}
