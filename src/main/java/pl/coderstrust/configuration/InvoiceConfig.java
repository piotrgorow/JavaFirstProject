package pl.coderstrust.configuration;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.coderstrust.accounting.util.json.InvoiceJsonConverter;
import pl.coderstrust.database.Database;
import pl.coderstrust.database.FileHelper;
import pl.coderstrust.database.InFileDatabase;
import pl.coderstrust.database.InMemoryDatabase;

@Configuration
public class InvoiceConfig {

  public static final String INVOICE_DATABASE_FILE = "src/main/resources/Invoices.txt";
  private boolean useInMemoryDatabase = true;

  @Bean
  public Database database() throws IOException {
    if (useInMemoryDatabase) {
      return new InMemoryDatabase();
    }
    return new InFileDatabase(new FileHelper(), new InvoiceJsonConverter());
  }
}
