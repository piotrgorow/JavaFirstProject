package pl.coderstrust.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Address;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-sql")
@Slf4j
public class SqlDatabase implements Database {

  private DataSourceProperties properties;

  public SqlDatabase(DataSourceProperties properties) {
    this.properties = properties;
  }

  private Connection createConnection() throws SQLException {
    return DriverManager.getConnection(properties.getUrl(), properties.getUsername(), properties.getPassword());
  }

  @Override
  public void saveInvoice(Invoice invoice) {
    if (invoice == null) {
      String message = "Invoice cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    try (Connection connection = createConnection()) {
      try (Statement statement = connection.createStatement()) {
        connection.setAutoCommit(false);
        Long sellerAddressId = getDatabaseId(statement, "address");
        insertIntoAddress(statement, sellerAddressId, invoice.getSeller().getAddress());
        Long sellerId = getDatabaseId(statement, "company");
        insertIntoCompany(statement, sellerId, invoice.getSeller(), sellerAddressId);
        Long buyerAddressId = getDatabaseId(statement, "address");
        insertIntoAddress(statement, buyerAddressId, invoice.getBuyer().getAddress());
        Long buyerId = getDatabaseId(statement, "company");
        insertIntoCompany(statement, buyerId, invoice.getBuyer(), buyerAddressId);
        Long invoiceId = getDatabaseId(statement, "invoice");
        invoice.setId(invoiceId);
        insertIntoInvoice(statement, invoice, buyerId, sellerId);
        insertIntoInvoiceEntry(statement, invoice);
        connection.commit();
        connection.setAutoCommit(true);
        log.info("Saving invoice with ID = {}", invoice.getId());
      } catch (Exception e) {
        log.error("Error saving invoice", e);
        connection.rollback();
        connection.setAutoCommit(true);
      }
    } catch (SQLException e) {
      String message = "Illegal connection exception";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  private Long getDatabaseId(Statement statement, String databaseName) throws SQLException {
    ResultSet resultSet = statement.executeQuery(String.format("SELECT MAX(%s_id) id from %s", databaseName,
        databaseName));
    if (resultSet.next()) {
      Long id = resultSet.getLong("id");
      id++;
      return id;
    } else {
      return 1L;
    }
  }

  private void insertIntoAddress(Statement statement, Long id, Address address) throws SQLException {
    statement.execute(String.format("INSERT INTO address (address_id, address_city, address_country_code, "
            + "address_postal_code, address_street) VALUES (%d, '%s', '%s', '%s', '%s')", id,
        address.getCity(), address.getCountryCode(), address.getPostalCode(), address.getStreetAddress()));
  }

  private void insertIntoCompany(Statement statement, Long id, Company company, Long addressId) throws SQLException {
    statement.execute(String.format("INSERT INTO company (company_id, company_name, "
            + "company_tax_identification_number, company_address_id) VALUES (%d, '%s', '%s', %d)", id,
        company.getName(), company.getTaxIdentificationNumber(), addressId));
  }

  private void insertIntoInvoice(Statement statement, Invoice invoice, Long buyerId, Long sellerId)
      throws SQLException {
    statement.execute(String.format("INSERT INTO invoice (invoice_id, invoice_date, invoice_number, "
            + "invoice_buyer_id, invoice_seller_id) VALUES (%d, '%s', '%s', %d, %d)", invoice.getId(),
        invoice.getDate().toString(), invoice.getInvoiceNumber(), buyerId, sellerId));
  }

  private void insertIntoInvoiceEntry(Statement statement, Invoice invoice) throws SQLException {
    Long invoiceEntryId;
    for (InvoiceEntry invoiceEntry : invoice.getInvoiceEntries()) {
      invoiceEntryId = getDatabaseId(statement, "invoice_entry");
      statement.execute(String.format("INSERT INTO invoice_entry (invoice_entry_id, invoice_entry_description, "
              + "invoice_entry_quantity, invoice_entry_value, invoice_entry_vat_rate, invoice_entry_vat_value, "
              + "invoice_id) VALUES (%d, '%s', %d, %s, '%s', %s, %d)",
          invoiceEntryId, invoiceEntry.getDescription(), invoiceEntry.getQuantity(), invoiceEntry.getValue(),
          invoiceEntry.getVatRate(), invoiceEntry.getVatValue(), invoice.getId()));
    }
  }

  @Override
  public Invoice getInvoiceById(Long id) {
    Invoice invoice;
    StringBuilder invoiceQuery = new StringBuilder(getInvoiceQuery())
        .append(String.format(" where invoice_id = %d", id));
    try (Connection connection = createConnection()) {
      try (Statement statement = connection.createStatement()) {
        ResultSet resultSet = statement.executeQuery(invoiceQuery.toString());
        if (resultSet.next()) {
          invoice = getInvoice(resultSet);
          addInvoiceEntries(connection, invoice);
          log.info("Reading invoice with ID = {}", id);
          return invoice;
        }
        log.warn("Invoice with ID = {} does not exist", id);
        return null;
      }
    } catch (SQLException e) {
      String message = "Illegal connection exception";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  @Override
  public Collection<Invoice> getInvoices() {
    List<Invoice> result = new ArrayList<>();
    Invoice invoice;
    try (Connection connection = createConnection()) {
      try (Statement statement = connection.createStatement()) {
        ResultSet resultSet = statement.executeQuery(getInvoiceQuery());
        while (resultSet.next()) {
          invoice = getInvoice(resultSet);
          addInvoiceEntries(connection, invoice);
          result.add(invoice);
        }
        log.info("Reading all invoices");
        return result;
      }
    } catch (SQLException e) {
      String message = "Illegal connection exception";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  private String getInvoiceQuery() {
    return "SELECT invoice.invoice_id, invoice_date, invoice_number, "
        + "seller.company_id seller_id, seller.company_name seller_name, seller.company_tax_identification_number "
        + "seller_tax_identification_number, buyer.company_id buyer_id, buyer.company_name buyer_name, "
        + "buyer.company_tax_identification_number buyer_tax_identification_number, seller_address.address_id "
        + "seller_address_id, seller_address.address_city seller_city, seller_address.address_country_code "
        + "seller_country_code, seller_address.address_postal_code seller_postal_code, seller_address.address_street "
        + "seller_street, buyer_address.address_id buyer_address_id, buyer_address.address_city buyer_city, "
        + "buyer_address.address_country_code buyer_country_code, buyer_address.address_postal_code buyer_postal_code, "
        + "buyer_address.address_street buyer_street FROM invoice join company seller on invoice_seller_id = "
        + "seller.company_id join company buyer on invoice_buyer_id = buyer.company_id join address seller_address on "
        + "seller.company_address_id = seller_address.address_id join address buyer_address on buyer.company_address_id"
        + " = buyer_address.address_id";
  }

  private Invoice getInvoice(ResultSet resultSet) throws SQLException {
    Invoice invoice = new Invoice(resultSet.getString("invoice_number"),
        resultSet.getDate("invoice_date").toLocalDate(),
        getCompany(resultSet, "seller"),
        getCompany(resultSet, "buyer"));
    invoice.setId(resultSet.getLong("invoice_id"));
    return invoice;
  }

  private Company getCompany(ResultSet resultSet, String customer) throws SQLException {
    return new Company(resultSet.getLong(String.format("%s_id", customer)),
        resultSet.getString(String.format("%s_name", customer)),
        resultSet.getString(String.format("%s_tax_identification_number", customer)),
        getAddress(resultSet, customer));
  }

  private Address getAddress(ResultSet resultSet, String customer) throws SQLException {
    return new Address(resultSet.getLong(String.format("%s_address_id", customer)),
        resultSet.getString(String.format("%s_street", customer)),
        resultSet.getString(String.format("%s_postal_code", customer)),
        resultSet.getString(String.format("%s_city", customer)),
        resultSet.getString(String.format("%s_country_code", customer)));
  }

  private void addInvoiceEntries(Connection connection, Invoice invoice) {
    try (Statement statement = connection.createStatement()) {
      ResultSet resultSet = statement.executeQuery(getInvoiceEntryQuery(invoice.getId()));
      while (resultSet.next()) {
        invoice.addInvoiceEntry(resultSet.getLong("invoice_entry_id"),
            resultSet.getString("invoice_entry_description"),
            resultSet.getInt("invoice_entry_quantity"),
            resultSet.getBigDecimal("invoice_entry_value"),
            Vat.valueOf(resultSet.getString("invoice_entry_vat_rate"))
        );
      }
    } catch (Exception e) {
      throw new IllegalStateException("Illegal statement exception", e);
    }
  }

  private String getInvoiceEntryQuery(Long id) {
    return String.format("SELECT invoice_entry_id, invoice_entry_description, "
        + "invoice_entry_quantity, invoice_entry_value, invoice_entry_vat_rate, invoice_entry_vat_value "
        + "FROM invoice_entry where invoice_id = %d", id);
  }

  @Override
  public boolean updateInvoice(Long id, Invoice invoice) {
    if (invoice == null) {
      String message = "Invoice cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    Invoice invoiceFromDatabase;
    try (Connection connection = createConnection()) {
      try (Statement statement = connection.createStatement()) {
        try {
          invoiceFromDatabase = getInvoiceById(id);
          if (invoiceFromDatabase == null) {
            log.warn("Invoice with ID = {} does not exist", id);
            return false;
          }
          connection.setAutoCommit(false);
          invoice.setId(id);
          updateInvoice(statement, invoice);
          updateInvoiceEntry(statement, invoice);
          updateCompany(statement, invoice.getBuyer(), invoiceFromDatabase.getBuyer().getCompanyId());
          updateAddress(statement, invoice.getBuyer().getAddress(),
              invoiceFromDatabase.getBuyer().getAddress().getAddressId());
          updateCompany(statement, invoice.getSeller(), invoiceFromDatabase.getSeller().getCompanyId());
          updateAddress(statement, invoice.getSeller().getAddress(),
              invoiceFromDatabase.getSeller().getAddress().getAddressId());
          connection.commit();
          connection.setAutoCommit(true);
        } catch (Exception e) {
          log.error("Error updating invoice", e);
          connection.rollback();
          connection.setAutoCommit(true);
        }
        log.info("Updating invoice with ID = {}", id);
        return true;
      }
    } catch (Exception e) {
      String message = "Illegal connection exception";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  private void updateInvoice(Statement statement, Invoice invoice) throws SQLException {
    statement.execute(String.format("UPDATE invoice SET invoice_number = '%s', invoice_date = '%s' WHERE "
        + "invoice_id = %d", invoice.getInvoiceNumber(), invoice.getDate().toString(), invoice.getId()));
  }

  private void updateInvoiceEntry(Statement statement, Invoice invoice) throws SQLException {
    statement.execute(String.format("DELETE FROM invoice_entry WHERE invoice_id = %d", invoice.getId()));
    insertIntoInvoiceEntry(statement, invoice);
  }

  private void updateCompany(Statement statement, Company company, Long id) throws SQLException {
    statement.execute(String.format("UPDATE company SET company_name = '%s', company_tax_identification_number = '%s' "
            + "WHERE company_id = %d",
        company.getName(), company.getTaxIdentificationNumber(), id));
  }

  private void updateAddress(Statement statement, Address address, Long id) throws SQLException {
    statement.execute(String.format("UPDATE address SET address_street = '%s', address_postal_code = '%s',"
            + "address_city = '%s', address_country_code = '%s' WHERE address_id = %d", address.getStreetAddress(),
        address.getPostalCode(), address.getCity(), address.getCountryCode(), id));
  }

  @Override
  public boolean removeInvoiceById(Long id) {
    try (Connection connection = createConnection()) {
      try (Statement statement = connection.createStatement()) {
        Long sellerId = getCompanyId(statement, id, "seller");
        Long buyerId = getCompanyId(statement, id, "buyer");
        Long sellerAddressId = getAddressId(statement, sellerId);
        Long buyerAddressId = getAddressId(statement, buyerId);
        if ((sellerId == null) || (buyerId == null) || (sellerAddressId == null) || (buyerAddressId == null)) {
          log.error("ID cannot be null");
          return false;
        }
        try {
          connection.setAutoCommit(false);
          statement.execute(String.format("DELETE FROM invoice_entry where invoice_id = %s", id));
          statement.execute(String.format("DELETE FROM invoice where invoice_id = %s", id));
          statement.execute(String.format("DELETE FROM company WHERE company_id in (%d, %d)", sellerId, buyerId));
          statement.execute(String.format("DELETE FROM address WHERE address_id in (%d, %d)",
              sellerAddressId, buyerAddressId));
          connection.commit();
          connection.setAutoCommit(true);
          log.info("Removing invoice with ID = {}", id);
        } catch (Exception e) {
          connection.rollback();
          connection.setAutoCommit(true);
          log.error("Error deleting invoice", e);
          return false;
        }
        return true;
      }
    } catch (SQLException e) {
      String message = "Illegal connection exception";
      log.error(message, e);
      throw new IllegalStateException(message, e);
    }
  }

  private Long getAddressId(Statement statement, Long id) throws SQLException {
    ResultSet resultSet =
        statement.executeQuery(String.format("SELECT company_address_id FROM company where company_id = %d", id));
    if (resultSet.next()) {
      return resultSet.getLong("company_address_id");
    } else {
      return null;
    }
  }

  private Long getCompanyId(Statement statement, Long id, String customer) throws SQLException {
    ResultSet resultSet =
        statement.executeQuery(String.format("SELECT invoice_%s_id FROM invoice "
            + "where invoice_id = %d", customer, id));
    if (resultSet.next()) {
      return resultSet.getLong(String.format("invoice_%s_id", customer));
    } else {
      return null;
    }
  }
}
