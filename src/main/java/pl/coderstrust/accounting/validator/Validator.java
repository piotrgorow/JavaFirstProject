package pl.coderstrust.accounting.validator;

import java.util.List;

public interface Validator<T> {

  List<String> validate(T obj);
}
