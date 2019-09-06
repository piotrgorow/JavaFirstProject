package pl.coderstrust.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.coderstrust.model.Address;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-sql-template")
@Transactional
@Slf4j
public class SqlTemplateDatabase implements Database {

  private JdbcTemplate jdbcTemplate;

  SqlTemplateDatabase(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void saveInvoice(Invoice invoice) {
    if (invoice == null) {
      String message = "Invoice cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    Long sellerAddressId = getDatabaseId("address");
    insertAddress(sellerAddressId, invoice.getSeller().getAddress());
    Long sellerCompanyId = getDatabaseId("company");
    insertCompany(sellerCompanyId, invoice.getSeller(), sellerAddressId);
    Long buyerAddressId = getDatabaseId("address");
    insertAddress(buyerAddressId, invoice.getBuyer().getAddress());
    Long buyerCompanyId = getDatabaseId("company");
    insertCompany(buyerCompanyId, invoice.getBuyer(), buyerAddressId);
    Long invoiceId = getDatabaseId("invoice");
    invoice.setId(invoiceId);
    insertInvoice(invoice, buyerCompanyId, sellerCompanyId);
    insertEntries(invoice);
    log.info("Saving invoice with ID = {}", invoice.getId());
  }

  private Long getDatabaseId(String databaseName) {
    String query = String.format("SELECT CASE WHEN MAX(%s_id) IS null THEN 1 ELSE MAX(%s_id) + 1 END id from %s",
        databaseName, databaseName, databaseName);
    return jdbcTemplate.queryForObject(query, new Object[]{},
        (resultSet, rowNum) -> (resultSet.getLong("id")));
  }

  private void insertAddress(Long id, Address address) {
    String query = "INSERT INTO address (address_id, address_city, address_country_code, address_postal_code, "
        + "address_street) VALUES (?, ?, ?, ?, ?)";
    try {
      jdbcTemplate.update(query, id, address.getCity(), address.getCountryCode(), address.getPostalCode(),
          address.getStreetAddress());
    } catch (Exception e) {
      String message = "Error writing to address table";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  private void insertCompany(Long id, Company company, Long addressId) {
    String query = "INSERT INTO company (company_id, company_name, company_tax_identification_number, "
        + "company_address_id) VALUES (?, ?, ?, ?)";
    try {
      jdbcTemplate.update(query, id, company.getName(), company.getTaxIdentificationNumber(), addressId);
    } catch (Exception e) {
      String message = "Error writing to company table";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  private void insertInvoice(Invoice invoice, Long buyerId, Long sellerId) {
    String query = "INSERT INTO invoice (invoice_id, invoice_date, invoice_number, invoice_buyer_id, "
        + "invoice_seller_id) VALUES (?, ?, ?, ?, ?)";
    try {
      jdbcTemplate.update(query, invoice.getId(), invoice.getDate(), invoice.getInvoiceNumber(), buyerId, sellerId);
    } catch (Exception e) {
      String message = "Error writing to invoice table";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  private void insertEntries(Invoice invoice) {
    String query = "INSERT INTO invoice_entry (invoice_entry_id, invoice_entry_description, invoice_entry_quantity, "
        + "invoice_entry_value, invoice_entry_vat_rate, invoice_entry_vat_value, invoice_id) VALUES "
        + "(?, ?, ?, ?, ?, ?, ?)";
    Long id = getDatabaseId("invoice_entry");
    try {
      for (InvoiceEntry entry : invoice.getInvoiceEntries()) {
        jdbcTemplate.update(query, id, entry.getDescription(), entry.getQuantity(), entry.getValue(),
            entry.getVatRate().toString(), entry.getVatValue(), invoice.getId());
        id = getDatabaseId("invoice_entry");
      }
    } catch (Exception e) {
      String message = "Error writing to invoice_entry table";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  @Override
  public Invoice getInvoiceById(Long id) {
    String query = "SELECT * FROM invoice WHERE invoice_id = ?";
    try {
      log.info("Reading invoice with ID = {}", id);
      return jdbcTemplate.queryForObject(query, new Object[]{id}, (resultSet, rowNum) -> getInvoice(resultSet));
    } catch (Exception e) {
      log.warn("Invoice with ID = {} does not exist", id);
      return null;
    }
  }

  @Override
  public Collection<Invoice> getInvoices() {
    String query = "SELECT * FROM invoice";
    log.info("Reading all invoices");
    return jdbcTemplate.query(query, (resultSet, rowNum) -> getInvoice(resultSet));
  }

  private Invoice getInvoice(ResultSet resultSet) throws SQLException {
    Invoice invoice = new Invoice(
        resultSet.getString("invoice_number"),
        LocalDate.parse(resultSet.getString("invoice_date")),
        getCompany(resultSet.getLong("invoice_seller_id")),
        getCompany(resultSet.getLong("invoice_buyer_id"))
    );
    invoice.setId(resultSet.getLong("invoice_id"));
    for (InvoiceEntry entry : getEntries(invoice.getId())) {
      invoice.addInvoiceEntry(entry.getInvoiceEntryId(), entry.getDescription(), entry.getQuantity(),
          entry.getValue(), entry.getVatRate());
    }
    return invoice;
  }

  private Address getAddress(Long id) {
    String query = "SELECT * FROM address WHERE address_id = ?";
    return jdbcTemplate.queryForObject(query, new Object[]{id}, (resultSet, rowNum) ->
        new Address(
            resultSet.getLong("address_id"),
            resultSet.getString("address_street"),
            resultSet.getString("address_postal_code"),
            resultSet.getString("address_city"),
            resultSet.getString("address_country_code")
        ));
  }

  private Company getCompany(Long id) {
    String query = "SELECT * FROM company WHERE company_id = ?";
    return jdbcTemplate.queryForObject(query, new Object[]{id}, (resultSet, rowNum) ->
        new Company(
            resultSet.getLong("company_id"),
            resultSet.getString("company_name"),
            resultSet.getString("company_tax_identification_number"),
            getAddress(resultSet.getLong("company_address_id"))
        ));
  }

  private Collection<InvoiceEntry> getEntries(Long id) {
    String query = String.format("SELECT * FROM invoice_entry WHERE invoice_id = %d", id);
    return jdbcTemplate.query(query, (resultSet, rowNum) ->
        new InvoiceEntry(
            resultSet.getLong("invoice_entry_id"),
            resultSet.getString("invoice_entry_description"),
            resultSet.getInt("invoice_entry_quantity"),
            resultSet.getBigDecimal("invoice_entry_value"),
            Vat.valueOf(resultSet.getString("invoice_entry_vat_rate"))
        ));
  }

  @Override
  public boolean updateInvoice(Long id, Invoice invoice) {
    if (invoice == null) {
      String message = "Invoice cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    Invoice invoiceFromDatabase = getInvoiceById(id);
    if (invoiceFromDatabase == null) {
      log.warn("Invoice with ID = {} does not exist", id);
      return false;
    }
    invoice.setId(id);
    updateInvoice(invoice);
    updateInvoiceEntries(invoice);
    updateCompany(invoice.getSeller(), invoiceFromDatabase.getSeller().getCompanyId());
    updateAddress(invoice.getSeller().getAddress(), invoiceFromDatabase.getSeller().getAddress().getAddressId());
    updateCompany(invoice.getBuyer(), invoiceFromDatabase.getBuyer().getCompanyId());
    updateAddress(invoice.getBuyer().getAddress(), invoiceFromDatabase.getBuyer().getAddress().getAddressId());
    log.info("Updating invoice with ID = {}", id);
    return true;
  }

  private void updateInvoice(Invoice invoice) {
    try {
      String query = "UPDATE invoice SET invoice_number = ?, invoice_date = ? where invoice_id = ?";
      jdbcTemplate.update(query, invoice.getInvoiceNumber(), invoice.getDate(), invoice.getId());
    } catch (Exception e) {
      String message = "Illegal state exception";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  private void updateInvoiceEntries(Invoice invoice) {
    deleteInvoiceEntries(invoice.getId());
    insertEntries(invoice);
  }

  private void updateAddress(Address address, Long id) {
    try {
      String query = "UPDATE address SET address_street = ?, address_postal_code = ?, address_city = ?, "
          + "address_country_code = ? where address_id = ?";
      jdbcTemplate.update(query, address.getStreetAddress(), address.getPostalCode(), address.getCity(),
          address.getCountryCode(), id);
    } catch (Exception e) {
      String message = "Illegal state exception";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  private void updateCompany(Company company, Long id) {
    String query = "UPDATE company SET company_name = ?, company_tax_identification_number = ? "
        + "where company_id = ?";
    try {
      jdbcTemplate.update(query, company.getName(), company.getTaxIdentificationNumber(), id);
    } catch (Exception e) {
      String message = "Illegal state exception";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  @Override
  public boolean removeInvoiceById(Long id) {
    Invoice invoice = getInvoiceById(id);
    if (invoice == null) {
      log.warn("Invoice with ID = {} does not exist", id);
      return false;
    }
    deleteInvoiceEntries(id);
    deleteInvoice(id);
    deleteCompany(invoice.getSeller().getCompanyId());
    deleteAddress(invoice.getSeller().getAddress().getAddressId());
    deleteCompany(invoice.getBuyer().getCompanyId());
    deleteAddress(invoice.getBuyer().getAddress().getAddressId());
    log.info("Updating invoice with ID = {}", id);
    return true;
  }

  private void deleteInvoice(Long id) {
    String query = "DELETE FROM invoice where invoice_id = ?";
    jdbcTemplate.update(query, id);
  }

  private void deleteInvoiceEntries(Long id) {
    String query = "DELETE FROM invoice_entry where invoice_id = ?";
    jdbcTemplate.update(query, id);
  }

  private void deleteCompany(Long id) {
    String query = "DELETE FROM company where company_id = ?";
    jdbcTemplate.update(query, id);
  }

  private void deleteAddress(Long id) {
    String query = "DELETE FROM address where address_id =?";
    jdbcTemplate.update(query, id);
  }
}
