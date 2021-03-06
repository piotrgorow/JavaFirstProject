package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.coderstrust.model.Invoice;

@DisplayName("InMemoryDatabase Test")
class InMemoryDatabaseTest {

  @Test
  @DisplayName("Should save simple invoice")
  void shouldSaveSimpleInvoice() {
    // Given
    Invoice sampleInvoice = InvoiceTestUtil.sampleInvoice();
    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    inMemoryDatabase.saveInvoice(sampleInvoice);
    Set<Invoice> expected = new HashSet<>();
    expected.add(sampleInvoice);

    // When
    Collection<Invoice> result = inMemoryDatabase.getInvoices();

    // Then
    for (Invoice invoice : result) {
      assertTrue(expected.contains(invoice));
    }
  }

  @Test
  @DisplayName("Should throw exception if the invoice is null")
  void shouldThrowExceptionIfInvoiceIsNull() {
    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    assertThrows(IllegalArgumentException.class, () -> inMemoryDatabase.saveInvoice(null));
  }

  @DisplayName("Should return empty collection for no invoice in database")
  @Test
  void shouldReturnEmptyCollectionForEmptyDatabase() {
    // Given
    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();

    // When
    Collection<Invoice> result = inMemoryDatabase.getInvoices();

    // Then
    assertTrue(result.isEmpty());
  }

  @ParameterizedTest
  @MethodSource("getInvoiceByIdParameters")
  @DisplayName("Should return invoice by ID")
  void shouldReturnInvoiceById(Long id, Invoice expected) {
    // Given
    Invoice sampleInvoice1 = InvoiceTestUtil.sampleInvoiceFromFile();
    Invoice sampleInvoice2 = InvoiceTestUtil.sampleInvoiceFromFile2();
    Invoice sampleInvoice3 = InvoiceTestUtil.sampleInvoiceFromFile3();
    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    inMemoryDatabase.saveInvoice(sampleInvoice1);
    inMemoryDatabase.saveInvoice(sampleInvoice2);
    inMemoryDatabase.saveInvoice(sampleInvoice3);

    // When
    Invoice result = inMemoryDatabase.getInvoiceById(id);

    // Then
    assertEquals(expected, result);
  }

  private static Stream<Arguments> getInvoiceByIdParameters() {
    return Stream.of(
        Arguments.of(1L, InvoiceTestUtil.sampleInvoiceFromFile()),
        Arguments.of(2L, InvoiceTestUtil.sampleInvoiceFromFile2()),
        Arguments.of(3L, InvoiceTestUtil.sampleInvoiceFromFile3())
    );
  }

  @Test
  @DisplayName("Should return null when invoice with given ID does not exist")
  void shouldReturnNullWhenInvoiceWithGivenIdDoesNotExist() {
    // Given
    Invoice sampleInvoice1 = InvoiceTestUtil.sampleInvoice();
    Invoice sampleInvoice2 = InvoiceTestUtil.sampleInvoice2();
    Invoice sampleInvoice3 = InvoiceTestUtil.sampleInvoice3();
    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    inMemoryDatabase.saveInvoice(sampleInvoice1);
    inMemoryDatabase.saveInvoice(sampleInvoice2);
    inMemoryDatabase.saveInvoice(sampleInvoice3);

    // When
    Invoice result = inMemoryDatabase.getInvoiceById(100L);

    // Then
    assertNull(result);
  }

  @Test
  @DisplayName("Should update invoice")
  void shouldUpdateInvoice() {
    // Given
    Invoice invoiceToBeUpdated = InvoiceTestUtil.sampleInvoice();
    Invoice anotherInvoice = InvoiceTestUtil.sampleInvoice2();
    Invoice invoiceToUpdate = InvoiceTestUtil.sampleInvoice3();
    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    inMemoryDatabase.saveInvoice(invoiceToBeUpdated);
    inMemoryDatabase.saveInvoice(anotherInvoice);
    Invoice expected = InvoiceTestUtil.sampleInvoice3();

    // When
    inMemoryDatabase.updateInvoice(1L, invoiceToUpdate);
    Invoice result = inMemoryDatabase.getInvoiceById(1L);

    // Then
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Should return false for invoice which does not exists")
  void shouldReturnFalseWhenInvoiceToUpdateDoesNotExist() {
    // Given
    Invoice sampleInvoice1 = InvoiceTestUtil.sampleInvoice();
    Invoice sampleInvoice2 = InvoiceTestUtil.sampleInvoice2();
    Invoice invoiceToUpdate = InvoiceTestUtil.sampleInvoice3();
    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    inMemoryDatabase.saveInvoice(sampleInvoice1);
    inMemoryDatabase.saveInvoice(sampleInvoice2);

    // When
    boolean result = inMemoryDatabase.updateInvoice(10L, invoiceToUpdate);

    // Then
    assertFalse(result);
  }

  @Test
  @DisplayName("Should throw exception when invoice to replace is null")
  void shouldThrowExceptionWhenArgumentIsNull() {
    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    assertThrows(IllegalArgumentException.class,
        () -> inMemoryDatabase.updateInvoice(1L, null));
  }

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

  @Test
  @DisplayName("Should return null for invoice which was deleted")
  void shouldReturnNullForRemovedInvoice() {
    // Given
    Invoice sampleInvoice1 = InvoiceTestUtil.sampleInvoice();
    Invoice sampleInvoice2 = InvoiceTestUtil.sampleInvoice2();
    Invoice sampleInvoice3 = InvoiceTestUtil.sampleInvoice3();
    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    inMemoryDatabase.saveInvoice(sampleInvoice1);
    inMemoryDatabase.saveInvoice(sampleInvoice2);
    inMemoryDatabase.saveInvoice(sampleInvoice3);

    // When
    inMemoryDatabase.removeInvoiceById(1L);
    Invoice result = inMemoryDatabase.getInvoiceById(1L);

    // Then
    assertNull(result);
  }

  @Test
  @DisplayName("Should return false for removing invoice which not exists")
  void shouldReturnFalseForRemovingInvoiceWhichNotExists() {
    // Given
    Invoice sampleInvoice1 = InvoiceTestUtil.sampleInvoice();
    Invoice sampleInvoice2 = InvoiceTestUtil.sampleInvoice2();
    Invoice sampleInvoice3 = InvoiceTestUtil.sampleInvoice3();
    InMemoryDatabase inMemoryDatabase = new InMemoryDatabase();
    inMemoryDatabase.saveInvoice(sampleInvoice1);
    inMemoryDatabase.saveInvoice(sampleInvoice2);
    inMemoryDatabase.saveInvoice(sampleInvoice3);

    // When
    boolean result = inMemoryDatabase.removeInvoiceById(50L);

    // Then
    assertFalse(result);
  }
}
