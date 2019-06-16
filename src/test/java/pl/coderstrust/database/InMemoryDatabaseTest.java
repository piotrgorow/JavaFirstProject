package pl.coderstrust.database;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.coderstrust.model.Invoice;

@DisplayName("InMemoryDatabase Test")
class InMemoryDatabaseTest {

  @DisplayName("Should save simple invoice")
  @Test
  void shouldSaveSimpleInvoice(){

  }

  @Test
  void shouldThrowExceptionIfInvoiceIdIsNull(){

  }

  @Test
  void shouldReturnEmptyCollectionForEmptyDatabase(){

  }

  @Test
  @DisplayName("Should return invoice by ID")
  void shouldReturnInvoiceById(){
    // Add more than one invoice here!
  }

  @Test
  void shouldReturnNullWhenInvoiceWithGivenIdDeosNotExist(){

  }

  @Test
  void shouldUpdateInvoice(){

  }

  @Test
  void shouldReturnFalseWhenInvoiceToUpdateDoesNotExist(){

  }

  @Test
  void shouldThrowExceptionWhenArgumentIsNull(){

  }

  //Remove invoice and tests!

  @Test
  @DisplayName("Should save and get two sample invoices")
  void shouldSaveTwoInvoices() {
    // Given
    Invoice sampleInvoice = InvoiceTestUtil.sampleInvoice();
    Invoice sampleInvoice2 = InvoiceTestUtil.sampleInvoice2();

    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    inMemoryDatabase.saveInvoice(sampleInvoice);
    inMemoryDatabase.saveInvoice(sampleInvoice2);

    Set<Invoice> expected = new HashSet<>();
    expected.add(sampleInvoice);
    expected.add(sampleInvoice2);

    // When
    Collection<Invoice> result = inMemoryDatabase.getInvoices();

    // Then
    for (Invoice invoice : result) {
      assertTrue(expected.contains(invoice));
    }
  }
}
