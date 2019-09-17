package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mongo Database Test")
class MongoDatabaseTest {

  @Mock
  private MongoTemplate mongoTemplate;
  @InjectMocks
  private MongoDatabase mongoDatabase;

  @Test
  @DisplayName("Should save simple invoice")
  void shouldSaveSimpleInvoice() {
    // Given
    Invoice sampleInvoice = InvoiceTestUtil.sampleInvoice();
    List<Invoice> expected = new ArrayList<>();
    expected.add(sampleInvoice);
    when(mongoTemplate.findAll(Invoice.class)).thenReturn(expected);
    when(mongoTemplate.save(any(Invoice.class))).thenReturn(sampleInvoice);

    // When
    mongoDatabase.saveInvoice(sampleInvoice);
    Collection<Invoice> result = mongoDatabase.getInvoices();

    // Then
    for (Invoice invoice : result) {
      assertTrue(expected.contains(invoice));
    }
    verify(mongoTemplate).findAll(Invoice.class);
    verify(mongoTemplate).save(any(Invoice.class));
  }

  @Test
  @DisplayName("Should throw exception if the invoice is null")
  void shouldThrowExceptionIfInvoiceIsNull() {
    assertThrows(IllegalArgumentException.class, () -> mongoDatabase.saveInvoice(null));
  }

  @Test
  @DisplayName("Should return empty collection for no invoice in database")
  void shouldReturnEmptyCollectionForEmptyDatabase() {
    // Given
    List<Invoice> expected = new ArrayList<>();
    when(mongoTemplate.findAll(Invoice.class)).thenReturn(expected);

    // When
    Collection<Invoice> result = mongoDatabase.getInvoices();

    // Then
    assertTrue(result.isEmpty());
    verify(mongoTemplate).findAll(Invoice.class);
  }

  @ParameterizedTest
  @MethodSource("getInvoiceByIdParameters")
  @DisplayName("Should return invoice by ID")
  void shouldReturnInvoiceById(Long id, Invoice expected) {
    // Given
    if (id == 1L) {
      when(mongoTemplate.findById("1", Invoice.class)).thenReturn(InvoiceTestUtil.sampleInvoiceFromFile());
    } else if (id == 2L) {
      when(mongoTemplate.findById("2", Invoice.class)).thenReturn(InvoiceTestUtil.sampleInvoiceFromFile2());
    } else {
      when(mongoTemplate.findById("3", Invoice.class)).thenReturn(InvoiceTestUtil.sampleInvoiceFromFile3());
    }

    // When
    Invoice result = mongoDatabase.getInvoiceById(id);

    // Then
    assertEquals(expected, result);
    verify(mongoTemplate).findById(String.valueOf(id), Invoice.class);
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
    when(mongoTemplate.findById("100", Invoice.class)).thenReturn(null);

    // When
    Invoice result = mongoDatabase.getInvoiceById(100L);

    // Then
    assertNull(result);
    verify(mongoTemplate).findById("100", Invoice.class);
  }

  @Test
  @DisplayName("Should update invoice")
  void shouldUpdateInvoice() {
    // Given
    Invoice expected = InvoiceTestUtil.sampleInvoiceFromFile3();
    when(mongoTemplate.exists(any(Query.class), eq(Invoice.class))).thenReturn(true);
    when(mongoTemplate.save(any(Invoice.class))).thenReturn(expected);
    when(mongoTemplate.findById("1", Invoice.class)).thenReturn(expected);
    Invoice invoiceToUpdate = InvoiceTestUtil.sampleInvoice3();

    // When
    mongoDatabase.updateInvoice(1L, invoiceToUpdate);
    Invoice result = mongoDatabase.getInvoiceById(1L);

    // Then
    assertEquals(expected, result);
    verify(mongoTemplate).exists(any(Query.class), eq(Invoice.class));
    verify(mongoTemplate).save(invoiceToUpdate);
    verify(mongoTemplate).findById("1", Invoice.class);
  }

  @Test
  @DisplayName("Should return false for invoice which does not exists")
  void shouldReturnFalseWhenInvoiceToUpdateDoesNotExist() {
    // Given
    Invoice invoiceToUpdate = InvoiceTestUtil.sampleInvoice3();
    when(mongoTemplate.exists(any(Query.class), eq(Invoice.class))).thenReturn(false);

    // When
    boolean result = mongoDatabase.updateInvoice(10L, invoiceToUpdate);

    // Then
    assertFalse(result);
    verify(mongoTemplate).exists(any(Query.class), eq(Invoice.class));
  }

  @Test
  @DisplayName("Should throw exception when invoice to replace is null")
  void shouldThrowExceptionWhenArgumentIsNull() {
    assertThrows(IllegalArgumentException.class,
        () -> mongoDatabase.updateInvoice(1L, null));
  }

  @Test
  @DisplayName("Should save and get two sample invoices")
  void shouldSaveTwoInvoices() {
    // Given
    Invoice sampleInvoice = InvoiceTestUtil.sampleInvoice();
    Invoice sampleInvoice2 = InvoiceTestUtil.sampleInvoice2();
    when(mongoTemplate.save(sampleInvoice)).thenReturn(sampleInvoice);
    when(mongoTemplate.save(sampleInvoice2)).thenReturn(sampleInvoice2);
    mongoDatabase.saveInvoice(sampleInvoice);
    mongoDatabase.saveInvoice(sampleInvoice2);
    List<Invoice> expected = new ArrayList<>();
    expected.add(sampleInvoice);
    expected.add(sampleInvoice2);
    when(mongoTemplate.findAll(Invoice.class)).thenReturn(expected);

    // When
    Collection<Invoice> result = mongoDatabase.getInvoices();

    // Then
    for (Invoice invoice : result) {
      assertTrue(expected.contains(invoice));
    }
    verify(mongoTemplate).save(sampleInvoice);
    verify(mongoTemplate).save(sampleInvoice2);
    verify(mongoTemplate).findAll(Invoice.class);
  }

  @Test
  @DisplayName("Should return null for invoice which was deleted")
  void shouldReturnNullForRemovedInvoice() {
    // Given
    Invoice sampleInvoice = InvoiceTestUtil.sampleInvoiceFromFile();
    when(mongoTemplate.findById("1", Invoice.class)).thenReturn(null);
    when(mongoTemplate.exists(any(Query.class), eq(Invoice.class))).thenReturn(true);
    when(mongoTemplate.remove(any(Query.class), eq(Invoice.class))).thenReturn(null);

    // When
    mongoDatabase.removeInvoiceById(1L);
    Invoice result = mongoDatabase.getInvoiceById(1L);

    // Then
    assertNull(result);
    verify(mongoTemplate).findById("1", Invoice.class);
    verify(mongoTemplate).exists(any(Query.class), eq(Invoice.class));
    verify(mongoTemplate).remove(any(Query.class), eq(Invoice.class));
  }

  @Test
  @DisplayName("Should return false for removing invoice which not exists")
  void shouldReturnFalseForRemovingInvoiceWhichNotExists() {
    // Given
    when(mongoTemplate.exists(any(Query.class), eq(Invoice.class))).thenReturn(false);

    // When
    boolean result = mongoDatabase.removeInvoiceById(50L);

    // Then
    assertFalse(result);
    verify(mongoTemplate).exists(any(Query.class), eq(Invoice.class));
  }
}
