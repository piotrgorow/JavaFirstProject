package pl.coderstrust.accounting.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import pl.coderstrust.model.Invoice;

public class InvoiceJsonConverter {

  private static ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public static String toJson(Invoice invoice) throws IOException {
    return objectMapper.writeValueAsString(invoice);
  }

  public static Invoice fromJson(String json) throws IOException {
    return objectMapper.readValue(json, Invoice.class);
  }
}