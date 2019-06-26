package pl.coderstrust.accounting.validator;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import pl.coderstrust.accounting.Utils;
import pl.coderstrust.model.Address;

public class AddressValidator implements Validator<Address> {

  private final static Pattern POSTAL_CODE_PATTERN = Pattern.compile("\\d{2}-\\d{3}");

  @Override
  public List<String> validate(Address address) {
    List<String> result = new LinkedList<>();
    if (Utils.isNullOrEmpty(address.getCity())) {
      result.add("City must not be null or empty");
    }
    if (Utils.isNullOrEmpty(address.getStreetAddress())) {
      result.add("Street address must not be null or empty");
    }
    if (Utils.isNullOrEmpty(address.getCountryCode())) {
      result.add("Country must not be null or empty");
    } else {
      if (Arrays.stream(Locale.getISOCountries())
          .noneMatch(s -> s.equals(address.getCountryCode()))) {
        result.add("Country must be in the ISO format");
      }
    }
    if (Utils.isNullOrEmpty(address.getPostalCode())) {
      result.add("Postal code must not be null or empty");
    } else {
      if (!POSTAL_CODE_PATTERN.matcher(address.getPostalCode()).matches()) {
        result.add("Postal code must be in the format XX-XXX");
      }
    }
    return result;
  }


}
