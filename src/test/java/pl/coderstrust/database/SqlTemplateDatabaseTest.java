package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
@DisplayName("SQL Template Database Test")
class SqlTemplateDatabaseTest {

  @Mock
  private JdbcTemplate jdbcTemplate;
  @InjectMocks
  private SqlTemplateDatabase sqlTemplateDatabase;

  @Test
  @DisplayName("Should save simple invoice")
  void shouldSaveSimpleInvoice() {
    // Given
    Invoice sampleInvoice = InvoiceTestUtil.sampleInvoiceFromFile();
    List<Invoice> expected = new ArrayList<>();
    expected.add(sampleInvoice);
    when(jdbcTemplate.query(eq("SELECT * FROM invoice"), any(RowMapper.class))).thenReturn(expected);
    when(jdbcTemplate.queryForObject(any(String.class), (Object[]) any(Object.class),
        any(RowMapper.class))).thenReturn(1L);
    when(jdbcTemplate.update(eq("INSERT INTO address (address_id, address_city, address_country_code, "
            + "address_postal_code, address_street) VALUES (?, ?, ?, ?, ?)"), any(Long.class), any(String.class),
        any(String.class), any(String.class), any(String.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("INSERT INTO company (company_id, company_name, company_tax_identification_number, "
            + "company_address_id) VALUES (?, ?, ?, ?)"), any(Long.class), any(String.class),
        any(String.class), any(Long.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("INSERT INTO invoice (invoice_id, invoice_date, invoice_number, invoice_buyer_id, "
            + "invoice_seller_id) VALUES (?, ?, ?, ?, ?)"), any(Long.class), any(LocalDate.class),
        any(String.class), any(Long.class), any(Long.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("INSERT INTO invoice_entry (invoice_entry_id, invoice_entry_description, "
            + "invoice_entry_quantity, invoice_entry_value, invoice_entry_vat_rate, invoice_entry_vat_value, "
            + "invoice_id) VALUES (?, ?, ?, ?, ?, ?, ?)"), any(Long.class),
        any(String.class), any(int.class), any(BigDecimal.class), any(String.class), any(BigDecimal.class),
        any(Long.class))).thenReturn(1);
    sqlTemplateDatabase.saveInvoice(sampleInvoice);

    // When
    Collection<Invoice> result = sqlTemplateDatabase.getInvoices();

    // Then
    assertEquals(expected, result);
    verify(jdbcTemplate).query(eq("SELECT * FROM invoice"), any(RowMapper.class));
    verify(jdbcTemplate, times(9)).queryForObject(any(String.class), (Object[]) any(Object.class),
        any(RowMapper.class));
    verify(jdbcTemplate, times(2)).update(eq("INSERT INTO address (address_id, address_city, address_country_code, "
            + "address_postal_code, address_street) VALUES (?, ?, ?, ?, ?)"), any(Long.class), any(String.class),
        any(String.class), any(String.class), any(String.class));
    verify(jdbcTemplate, times(2))
        .update(eq("INSERT INTO company (company_id, company_name, company_tax_identification_number, "
                + "company_address_id) VALUES (?, ?, ?, ?)"), any(Long.class), any(String.class),
            any(String.class), any(Long.class));
    verify(jdbcTemplate).update(eq("INSERT INTO invoice (invoice_id, invoice_date, invoice_number, invoice_buyer_id, "
            + "invoice_seller_id) VALUES (?, ?, ?, ?, ?)"), any(Long.class), any(LocalDate.class),
        any(String.class), any(Long.class), any(Long.class));
    verify(jdbcTemplate, times(3)).update(eq("INSERT INTO invoice_entry (invoice_entry_id, invoice_entry_description, "
            + "invoice_entry_quantity, invoice_entry_value, invoice_entry_vat_rate, invoice_entry_vat_value, "
            + "invoice_id) VALUES (?, ?, ?, ?, ?, ?, ?)"), any(Long.class),
        any(String.class), any(int.class), any(BigDecimal.class), any(String.class), any(BigDecimal.class),
        any(Long.class));
  }

  @Test
  @DisplayName("Should throw exception if the invoice is null")
  void shouldThrowExceptionIfInvoiceIsNull() {
    assertThrows(IllegalArgumentException.class, () -> sqlTemplateDatabase.saveInvoice(null));
  }

  @Test
  @DisplayName("Should return empty collection for no invoice in database")
  void shouldReturnEmptyCollectionForEmptyDatabase() {
    // Given
    List<Invoice> expected = new ArrayList<>();
    when(jdbcTemplate.query(eq("SELECT * FROM invoice"), any(RowMapper.class))).thenReturn(expected);

    // When
    Collection<Invoice> result = sqlTemplateDatabase.getInvoices();

    // Then
    assertTrue(result.isEmpty());
    verify(jdbcTemplate).query(eq("SELECT * FROM invoice"), any(RowMapper.class));
  }

  @ParameterizedTest
  @MethodSource("getInvoiceByIdParameters")
  @DisplayName("Should return invoice by ID")
  void shouldReturnInvoiceById(Long id, Invoice expected) {
    // Given
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenReturn(expected);

    // When
    Invoice result = sqlTemplateDatabase.getInvoiceById(id);

    // Then
    assertEquals(expected, result);
    verify(jdbcTemplate).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
  }

  private static Stream<Arguments> getInvoiceByIdParameters() {
    return Stream.of(
        Arguments.of(1L, InvoiceTestUtil.sampleInvoiceFromFile()),
        Arguments.of(2L, InvoiceTestUtil.sampleInvoiceFromFile2()),
        Arguments.of(3L, InvoiceTestUtil.sampleInvoiceFromFile3())
    );
  }

  @Test
  @DisplayName("Should return null when exception was threw")
  void shouldReturnNullWhenExceptionWasThrew() {
    // Given
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenThrow(IllegalStateException.class);

    // When
    Invoice invoice = sqlTemplateDatabase.getInvoiceById(1L);

    // Then
    assertNull(invoice);
    verify(jdbcTemplate).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
  }

  @Test
  @DisplayName("Should return null when invoice with given ID does not exist")
  void shouldReturnNullWhenInvoiceWithGivenIdDoesNotExist() {
    // Given
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenReturn(null);

    // When
    Invoice result = sqlTemplateDatabase.getInvoiceById(100L);

    // Then
    assertNull(result);
    verify(jdbcTemplate).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
  }

  @Test
  @DisplayName("Should update invoice")
  void shouldUpdateInvoice() {
    // Given
    Invoice expected = InvoiceTestUtil.sampleInvoice3();
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenReturn(expected);

    // When
    sqlTemplateDatabase.updateInvoice(1L, expected);
    Invoice result = sqlTemplateDatabase.getInvoiceById(1L);

    // Then
    assertEquals(expected, result);
    verify(jdbcTemplate, times(2)).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
  }

  @Test
  @DisplayName("Should return false for invoice which does not exists")
  void shouldReturnFalseWhenInvoiceToUpdateDoesNotExist() {
    // Given
    Invoice invoiceToUpdate = InvoiceTestUtil.sampleInvoice3();
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenReturn(null);

    // When
    boolean result = sqlTemplateDatabase.updateInvoice(10L, invoiceToUpdate);

    // Then
    assertFalse(result);
    verify(jdbcTemplate).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
  }

  @Test
  @DisplayName("Should throw exception when invoice to replace is null")
  void shouldThrowExceptionWhenArgumentIsNull() {
    assertThrows(IllegalArgumentException.class, () -> sqlTemplateDatabase.updateInvoice(1L, null));
  }

  @Test
  @DisplayName("Should throw exception when invoice table was updated")
  void shouldThrowExceptionWhenInvoiceTableWasUpdated() {
    // Given
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenReturn(InvoiceTestUtil.sampleInvoiceFromFile());
    when(jdbcTemplate.update(eq("UPDATE invoice SET invoice_number = ?, invoice_date = ? where invoice_id = ?"),
        any(String.class), any(LocalDate.class), any(Long.class))).thenThrow(IllegalStateException.class);

    // Then
    assertThrows(IllegalStateException.class, () -> sqlTemplateDatabase.updateInvoice(1L,
        InvoiceTestUtil.sampleInvoiceFromFile()));
    verify(jdbcTemplate).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
    verify(jdbcTemplate).update(eq("UPDATE invoice SET invoice_number = ?, invoice_date = ? where invoice_id = ?"),
        any(String.class), any(LocalDate.class), any(Long.class));
  }

  @Test
  @DisplayName("Should throw exception when company table was updated")
  void shouldThrowExceptionWhenCompanyTableWasUpdated() {
    // Given
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenReturn(InvoiceTestUtil.sampleInvoiceFromFile());
    when(jdbcTemplate.update(eq("UPDATE invoice SET invoice_number = ?, invoice_date = ? "
        + "where invoice_id = ?"), any(String.class), any(LocalDate.class), any(Long.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("DELETE FROM invoice_entry where invoice_id = ?"), any(Long.class))).thenReturn(1);
    when(jdbcTemplate.queryForObject(eq("SELECT CASE WHEN MAX(invoice_entry_id) IS null THEN 1 ELSE "
            + "MAX(invoice_entry_id) + 1 END id from invoice_entry"), (Object[]) any(Object.class),
        any(RowMapper.class))).thenReturn(1L);
    when(jdbcTemplate.update(eq("INSERT INTO invoice_entry (invoice_entry_id, invoice_entry_description, "
            + "invoice_entry_quantity, invoice_entry_value, invoice_entry_vat_rate, invoice_entry_vat_value, "
            + "invoice_id) VALUES (?, ?, ?, ?, ?, ?, ?)"), any(Long.class),
        any(String.class), any(int.class), any(BigDecimal.class), any(String.class), any(BigDecimal.class),
        any(Long.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("UPDATE company SET company_name = ?, company_tax_identification_number = ? "
        + "where company_id = ?"), any(String.class), any(String.class), any(Long.class)))
        .thenThrow(IllegalStateException.class);

    // Then
    assertThrows(IllegalStateException.class, () -> sqlTemplateDatabase.updateInvoice(1L,
        InvoiceTestUtil.sampleInvoiceFromFile()));
    verify(jdbcTemplate).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
    verify(jdbcTemplate).update(eq("UPDATE invoice SET invoice_number = ?, invoice_date = ? "
        + "where invoice_id = ?"), any(String.class), any(LocalDate.class), any(Long.class));
    verify(jdbcTemplate).update(eq("DELETE FROM invoice_entry where invoice_id = ?"), any(Long.class));
    verify(jdbcTemplate, times(4)).queryForObject(eq("SELECT CASE WHEN MAX(invoice_entry_id) IS null THEN 1 ELSE "
            + "MAX(invoice_entry_id) + 1 END id from invoice_entry"), (Object[]) any(Object.class),
        any(RowMapper.class));
    verify(jdbcTemplate, times(3)).update(eq("INSERT INTO invoice_entry (invoice_entry_id, invoice_entry_description, "
            + "invoice_entry_quantity, invoice_entry_value, invoice_entry_vat_rate, invoice_entry_vat_value, "
            + "invoice_id) VALUES (?, ?, ?, ?, ?, ?, ?)"), any(Long.class),
        any(String.class), any(int.class), any(BigDecimal.class), any(String.class), any(BigDecimal.class),
        any(Long.class));
    verify(jdbcTemplate).update(eq("UPDATE company SET company_name = ?, company_tax_identification_number = ? "
        + "where company_id = ?"), any(String.class), any(String.class), any(Long.class));
  }

  @Test
  @DisplayName("Should throw exception when address table was updated")
  void shouldThrowExceptionWhenAddressTableWasUpdated() {
    // Given
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenReturn(InvoiceTestUtil.sampleInvoiceFromFile());
    when(jdbcTemplate.update(eq("UPDATE invoice SET invoice_number = ?, invoice_date = ? "
        + "where invoice_id = ?"), any(String.class), any(LocalDate.class), any(Long.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("DELETE FROM invoice_entry where invoice_id = ?"), any(Long.class))).thenReturn(1);
    when(jdbcTemplate.queryForObject(eq("SELECT CASE WHEN MAX(invoice_entry_id) IS null THEN 1 ELSE MAX"
            + "(invoice_entry_id) + 1 END id from invoice_entry"), (Object[]) any(Object.class),
        any(RowMapper.class))).thenReturn(1L);
    when(jdbcTemplate.update(eq("INSERT INTO invoice_entry (invoice_entry_id, invoice_entry_description, "
            + "invoice_entry_quantity, invoice_entry_value, invoice_entry_vat_rate, invoice_entry_vat_value, "
            + "invoice_id) VALUES (?, ?, ?, ?, ?, ?, ?)"), any(Long.class),
        any(String.class), any(int.class), any(BigDecimal.class), any(String.class), any(BigDecimal.class),
        any(Long.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("UPDATE company SET company_name = ?, company_tax_identification_number = ? "
        + "where company_id = ?"), any(String.class), any(String.class), any(Long.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("UPDATE address SET address_street = ?, address_postal_code = ?, address_city = "
            + "?, address_country_code = ? where address_id = ?"), any(String.class), any(String.class),
        any(String.class), any(String.class), any(Long.class))).thenThrow(IllegalStateException.class);

    // Then
    assertThrows(IllegalStateException.class, () -> sqlTemplateDatabase.updateInvoice(1L,
        InvoiceTestUtil.sampleInvoiceFromFile()));
    verify(jdbcTemplate).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
    verify(jdbcTemplate).update(eq("UPDATE invoice SET invoice_number = ?, invoice_date = ? "
        + "where invoice_id = ?"), any(String.class), any(LocalDate.class), any(Long.class));
    verify(jdbcTemplate).update(eq("DELETE FROM invoice_entry where invoice_id = ?"), any(Long.class));
    verify(jdbcTemplate, times(4)).queryForObject(eq("SELECT CASE WHEN MAX(invoice_entry_id) IS null THEN 1 ELSE MAX"
            + "(invoice_entry_id) + 1 END id from invoice_entry"), (Object[]) any(Object.class),
        any(RowMapper.class));
    verify(jdbcTemplate, times(3)).update(eq("INSERT INTO invoice_entry (invoice_entry_id, invoice_entry_description, "
            + "invoice_entry_quantity, invoice_entry_value, invoice_entry_vat_rate, invoice_entry_vat_value, "
            + "invoice_id) VALUES (?, ?, ?, ?, ?, ?, ?)"), any(Long.class),
        any(String.class), any(int.class), any(BigDecimal.class), any(String.class), any(BigDecimal.class),
        any(Long.class));
    verify(jdbcTemplate).update(eq("UPDATE company SET company_name = ?, company_tax_identification_number = ? "
        + "where company_id = ?"), any(String.class), any(String.class), any(Long.class));
    verify(jdbcTemplate).update(eq("UPDATE address SET address_street = ?, address_postal_code = ?, address_city = "
            + "?, address_country_code = ? where address_id = ?"), any(String.class), any(String.class),
        any(String.class), any(String.class), any(Long.class));
  }

  @Test
  @DisplayName("Should remove invoice")
  void shouldRemoveInvoice() {
    // Given
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenReturn(InvoiceTestUtil.sampleInvoiceFromFile());
    when(jdbcTemplate.update(eq("DELETE FROM invoice_entry where invoice_id = ?"), any(Long.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("DELETE FROM invoice where invoice_id = ?"), any(Long.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("DELETE FROM company where company_id = ?"), any(Long.class))).thenReturn(1);
    when(jdbcTemplate.update(eq("DELETE FROM address where address_id =?"), any(Long.class))).thenReturn(1);

    // When
    boolean result = sqlTemplateDatabase.removeInvoiceById(1L);

    // Then
    assertTrue(result);
    verify(jdbcTemplate).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
    verify(jdbcTemplate).update(eq("DELETE FROM invoice_entry where invoice_id = ?"), any(Long.class));
    verify(jdbcTemplate).update(eq("DELETE FROM invoice where invoice_id = ?"), any(Long.class));
    verify(jdbcTemplate, times(2)).update(eq("DELETE FROM company where company_id = ?"), any(Long.class));
    verify(jdbcTemplate, times(2)).update(eq("DELETE FROM address where address_id =?"), any(Long.class));
  }

  @Test
  @DisplayName("Should return null for invoice which was deleted")
  void shouldReturnNullForRemovedInvoice() {
    // Given
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenReturn(null);

    // When
    sqlTemplateDatabase.removeInvoiceById(1L);
    Invoice result = sqlTemplateDatabase.getInvoiceById(1L);

    // Then
    assertNull(result);
    verify(jdbcTemplate, times(2)).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
  }

  @Test
  @DisplayName("Should return false for removing invoice which not exists")
  void shouldReturnFalseForRemovingInvoiceWhichNotExists() {
    // Given
    when(jdbcTemplate.queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class))).thenReturn(null);

    // When
    boolean result = sqlTemplateDatabase.removeInvoiceById(50L);

    // Then
    assertFalse(result);
    verify(jdbcTemplate).queryForObject(eq("SELECT * FROM invoice WHERE invoice_id = ?"),
        (Object[]) any(Object.class), any(RowMapper.class));
  }
}
