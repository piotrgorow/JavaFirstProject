package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
@DisplayName("HibernateDatabase Test")
class HibernateDatabaseTest {

  @Mock
  private InvoicesSpringDataRepository invoicesSpringDataRepository;
  @InjectMocks
  private HibernateDatabase hibernateDatabase;

  @Test
  @DisplayName("Should save simple invoice")
  void shouldSaveSimpleInvoice() {
    // Given
    Invoice sampleInvoice = InvoiceTestUtil.sampleInvoice();
    Set<Invoice> expected = new HashSet<>();
    expected.add(sampleInvoice);
    when(invoicesSpringDataRepository.findAll()).thenReturn(expected);

    // When
    Collection<Invoice> result = hibernateDatabase.getInvoices();

    // Then
    for (Invoice invoice : result) {
      assertTrue(expected.contains(invoice));
    }
    verify(invoicesSpringDataRepository).findAll();
  }

  @Test
  @DisplayName("Should throw exception if the invoice is null")
  void shouldThrowExceptionIfInvoiceIsNull() {
    assertThrows(IllegalArgumentException.class, () -> hibernateDatabase.saveInvoice(null));
  }

  @Test
  @DisplayName("Should return empty collection for no invoice in database")
  void shouldReturnEmptyCollectionForEmptyDatabase() {
    // Given
    List<Invoice> expected = new ArrayList<>();
    when(invoicesSpringDataRepository.findAll()).thenReturn(expected);

    // When
    Collection<Invoice> result = hibernateDatabase.getInvoices();

    // Then
    assertTrue(result.isEmpty());
    verify(invoicesSpringDataRepository).findAll();
  }

  @ParameterizedTest
  @MethodSource("getInvoiceByIdParameters")
  @DisplayName("Should return invoice by ID")
  void shouldReturnInvoiceById(Long id, Invoice expected) {
    // Given
    if (id == 1L) {
      when(invoicesSpringDataRepository.findById(1L)).thenReturn(Optional.of(InvoiceTestUtil.sampleInvoiceFromFile()));
    } else if (id == 2L) {
      when(invoicesSpringDataRepository.findById(2L)).thenReturn(Optional.of(InvoiceTestUtil.sampleInvoiceFromFile2()));
    } else {
      when(invoicesSpringDataRepository.findById(3L)).thenReturn(Optional.of(InvoiceTestUtil.sampleInvoiceFromFile3()));
    }

    // When
    Invoice result = hibernateDatabase.getInvoiceById(id);

    // Then
    assertEquals(expected, result);
    verify(invoicesSpringDataRepository, times(2)).findById(id);
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
    when(invoicesSpringDataRepository.findById(100L)).thenReturn(Optional.empty());

    // When
    Invoice result = hibernateDatabase.getInvoiceById(100L);

    // Then
    assertNull(result);
    verify(invoicesSpringDataRepository).findById(100L);
  }

  @Test
  @DisplayName("Should update invoice")
  void shouldUpdateInvoice() {
    // Given
    Invoice invoiceToUpdate = InvoiceTestUtil.sampleInvoice3();
    Invoice expected = InvoiceTestUtil.sampleInvoiceFromFile3();
    when(invoicesSpringDataRepository.findById(1L)).thenReturn(Optional.of(expected));
    when(invoicesSpringDataRepository.save(invoiceToUpdate)).thenReturn(expected);

    // When
    hibernateDatabase.updateInvoice(1L, invoiceToUpdate);
    Invoice result = hibernateDatabase.getInvoiceById(1L);

    // Then
    assertEquals(expected, result);
    verify(invoicesSpringDataRepository, times(3)).findById(1L);
    verify(invoicesSpringDataRepository).save(invoiceToUpdate);
  }

  @Test
  @DisplayName("Should return false for invoice which does not exists")
  void shouldReturnFalseWhenInvoiceToUpdateDoesNotExist() {
    // Given
    Invoice invoiceToUpdate = InvoiceTestUtil.sampleInvoice3();
    when(invoicesSpringDataRepository.findById(10L)).thenReturn(Optional.empty());

    // When
    boolean result = hibernateDatabase.updateInvoice(10L, invoiceToUpdate);

    // Then
    assertFalse(result);
    verify(invoicesSpringDataRepository).findById(10L);
  }

  @Test
  @DisplayName("Should throw exception when invoice to replace is null")
  void shouldThrowExceptionWhenArgumentIsNull() {
    assertThrows(IllegalArgumentException.class,
        () -> hibernateDatabase.updateInvoice(1L, null));
  }

  @Test
  @DisplayName("Should save and get two sample invoices")
  void shouldSaveTwoInvoices() {
    // Given
    Invoice sampleInvoice = InvoiceTestUtil.sampleInvoice();
    Invoice sampleInvoice2 = InvoiceTestUtil.sampleInvoice2();
    when(invoicesSpringDataRepository.save(sampleInvoice)).thenReturn(sampleInvoice);
    when(invoicesSpringDataRepository.save(sampleInvoice2)).thenReturn(sampleInvoice2);
    hibernateDatabase.saveInvoice(sampleInvoice);
    hibernateDatabase.saveInvoice(sampleInvoice2);
    Set<Invoice> expected = new HashSet<>();
    expected.add(sampleInvoice);
    expected.add(sampleInvoice2);
    when(invoicesSpringDataRepository.findAll()).thenReturn(expected);

    // When
    Collection<Invoice> result = hibernateDatabase.getInvoices();

    // Then
    for (Invoice invoice : result) {
      assertTrue(expected.contains(invoice));
    }
    verify(invoicesSpringDataRepository).save(sampleInvoice);
    verify(invoicesSpringDataRepository).save(sampleInvoice2);
    verify(invoicesSpringDataRepository).findAll();
  }

  @Test
  @DisplayName("Should return null for invoice which was deleted")
  void shouldReturnNullForRemovedInvoice() {
    // Given
    when(invoicesSpringDataRepository.findById(1L)).thenReturn(Optional.of(InvoiceTestUtil.sampleInvoiceFromFile()));
    doNothing().when(invoicesSpringDataRepository).deleteById(1L);

    // When
    hibernateDatabase.removeInvoiceById(1L);
    when(invoicesSpringDataRepository.findById(1L)).thenReturn(Optional.empty());
    Invoice result = hibernateDatabase.getInvoiceById(1L);

    // Then
    assertNull(result);
    verify(invoicesSpringDataRepository, times(2)).findById(1L);
    verify(invoicesSpringDataRepository).deleteById(1L);
  }

  @Test
  @DisplayName("Should return false for removing invoice which not exists")
  void shouldReturnFalseForRemovingInvoiceWhichNotExists() {
    // Given
    when(invoicesSpringDataRepository.findById(50L)).thenReturn(Optional.empty());

    // When
    boolean result = hibernateDatabase.removeInvoiceById(50L);

    // Then
    assertFalse(result);
    verify(invoicesSpringDataRepository).findById(50L);
  }
}
