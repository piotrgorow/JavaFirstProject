package pl.coderstrust.accounting.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.coderstrust.model.Invoice;

@Component
@Slf4j
public class InvoiceJsonConverter {

  private final ObjectMapper objectMapper;

  public InvoiceJsonConverter() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public String toJson(Invoice invoice) throws IOException {
    if (invoice == null) {
      String message = "Parameter invoice cannot be null.";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    String invoiceAsString = objectMapper.writeValueAsString(invoice);
    log.info("Invoice was converted to JSON: {}", invoiceAsString);
    return invoiceAsString;
  }

  public String toJsonAsList(Collection<Invoice> invoice) throws IOException {
    if (invoice == null) {
      String message = "Parameter invoice cannot be null.";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    String invoicesAsString = objectMapper.writeValueAsString(invoice);
    log.debug("The invoice list has been converted to JSON: {}", invoicesAsString);
    return invoicesAsString;
  }

  public Invoice fromJson(String json) throws IOException {
    if (json == null || json.equals("")) {
      log.debug("Parameter json is: {}", json);
      String message = "Parameter json cannot be null or empty.";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    log.debug("JSON to Invoice was converted: {}", json);
    return objectMapper.readValue(json, Invoice.class);
  }
}
