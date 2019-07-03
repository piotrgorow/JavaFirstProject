package pl.coderstrust.accounting.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pl.coderstrust.model.Invoice;

@Component
public class InvoiceJsonConverter {

  private final ObjectMapper objectMapper;

  public InvoiceJsonConverter() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public String toJson(Invoice invoice) throws IOException {
    if (invoice == null) {
      throw new IllegalArgumentException("Parameter invoice cannot be null.");
    }
    return objectMapper.writeValueAsString(invoice);
  }

  public String toJsonAsList(Collection<Invoice> invoice) throws IOException {
    if (invoice == null) {
      throw new IllegalArgumentException("Parameter invoice cannot be null.");
    }
    return objectMapper.writeValueAsString(invoice);
  }

  public Invoice fromJson(String json) throws IOException {
    if (json == null || json.equals("")) {
      throw new IllegalArgumentException("Parameter json cannot be null or empty.");
    }
    return objectMapper.readValue(json, Invoice.class);
  }
}
