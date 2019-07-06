package pl.coderstrust.accounting.validator;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import pl.coderstrust.accounting.Utils;
import pl.coderstrust.model.Company;

public class CompanyValidator implements Validator<Company> {

  private static final Pattern TAX_ID_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{2}-\\d{2}");

  @Override
  public List<String> validate(Company company) {
    List<String> result = new LinkedList<>();
    if (Utils.isNullOrEmpty(company.getName())) {
      result.add("Company name must not be null or empty");
    }
    if (Utils.isNullOrEmpty(company.getTaxIdentificationNumber())) {
      result.add("Company tax ID must not be null or empty");
    } else {
      if (!CompanyValidator.TAX_ID_PATTERN.matcher(company.getTaxIdentificationNumber())
          .matches()) {
        result.add("Company tax ID must be in the format XXX-XXX-XX-XX");
      }
    }
    return result;
  }
}
