package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
@DisplayName("SQL Database Test")
class SqlDatabaseTest {

  @Mock
  private DataSourceProperties properties;
  @InjectMocks
  private SqlDatabase sqlDatabase;

  @BeforeEach
  void setup() {
    when(properties.getUrl()).thenReturn("jdbc:h2:file:./java11");
    when(properties.getUsername()).thenReturn("sa");
    when(properties.getPassword()).thenReturn("");
    for (Invoice invoice : sqlDatabase.getInvoices()) {
      sqlDatabase.removeInvoiceById(invoice.getId());
    }
  }

  @Test
  @DisplayName("Should save simple invoice")
  void shouldSaveSimpleInvoice() {
    // Given
    Invoice sampleInvoice = InvoiceTestUtil.sampleInvoiceFromFile();
    sqlDatabase.saveInvoice(sampleInvoice);
    Set<Invoice> expected = new HashSet<>();
    expected.add(sampleInvoice);

    // When
    Collection<Invoice> result = sqlDatabase.getInvoices();

    // Then
    for (Invoice invoice : result) {
      assertTrue(expected.contains(invoice));
    }
  }

  @Test
  @DisplayName("Should throw exception if the invoice is null")
  void shouldThrowExceptionIfInvoiceIsNull() {
    assertThrows(IllegalArgumentException.class, () -> sqlDatabase.saveInvoice(null));
  }

  @Test
  @DisplayName("Should return empty collection for no invoice in database")
  void shouldReturnEmptyCollectionForEmptyDatabase() {
    // Given

    // When
    Collection<Invoice> result = sqlDatabase.getInvoices();

    // Then
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("Should return invoice by ID")
  void shouldReturnInvoiceById() {
    // Given
    Invoice expected = InvoiceTestUtil.sampleInvoiceFromFile();
    sqlDatabase.saveInvoice(expected);

    // When
    Invoice result = sqlDatabase.getInvoiceById(1L);

    // Then
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Should return null when invoice with given ID does not exist")
  void shouldReturnNullWhenInvoiceWithGivenIdDoesNotExist() {
    // Given
    Invoice sampleInvoice1 = InvoiceTestUtil.sampleInvoice();
    Invoice sampleInvoice2 = InvoiceTestUtil.sampleInvoice2();
    Invoice sampleInvoice3 = InvoiceTestUtil.sampleInvoice3();
    sqlDatabase.saveInvoice(sampleInvoice1);
    sqlDatabase.saveInvoice(sampleInvoice2);
    sqlDatabase.saveInvoice(sampleInvoice3);

    // When
    Invoice result = sqlDatabase.getInvoiceById(100L);

    // Then
    assertNull(result);
  }

  @Test
  @DisplayName("Should update invoice")
  void shouldUpdateInvoice() {
    // Given
    Invoice invoice = InvoiceTestUtil.sampleInvoice();
    Invoice expected = InvoiceTestUtil.sampleInvoice3();
    expected.setId(1L);
    sqlDatabase.saveInvoice(invoice);

    // When
    sqlDatabase.updateInvoice(1L, expected);
    Invoice result = sqlDatabase.getInvoiceById(1L);

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
    sqlDatabase.saveInvoice(sampleInvoice1);
    sqlDatabase.saveInvoice(sampleInvoice2);

    // When
    boolean result = sqlDatabase.updateInvoice(10L, invoiceToUpdate);

    // Then
    assertFalse(result);
  }

  @Test
  @DisplayName("Should throw exception when invoice to replace is null")
  void shouldThrowExceptionWhenArgumentIsNull() {
    assertThrows(IllegalArgumentException.class, () -> sqlDatabase.updateInvoice(1L, null));
  }

  @Test
  @DisplayName("Should return null for invoice which was deleted")
  void shouldReturnNullForRemovedInvoice() {
    // Given
    Invoice sampleInvoice1 = InvoiceTestUtil.sampleInvoice();
    Invoice sampleInvoice2 = InvoiceTestUtil.sampleInvoice2();
    Invoice sampleInvoice3 = InvoiceTestUtil.sampleInvoice3();
    sqlDatabase.saveInvoice(sampleInvoice1);
    sqlDatabase.saveInvoice(sampleInvoice2);
    sqlDatabase.saveInvoice(sampleInvoice3);

    // When
    sqlDatabase.removeInvoiceById(1L);
    Invoice result = sqlDatabase.getInvoiceById(1L);

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
    sqlDatabase.saveInvoice(sampleInvoice1);
    sqlDatabase.saveInvoice(sampleInvoice2);
    sqlDatabase.saveInvoice(sampleInvoice3);

    // When
    boolean result = sqlDatabase.removeInvoiceById(50L);

    // Then
    assertFalse(result);
  }
}
