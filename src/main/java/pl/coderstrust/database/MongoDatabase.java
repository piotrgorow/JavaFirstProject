package pl.coderstrust.database;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-mongo-db")
@Slf4j
public class MongoDatabase implements Database {

  private final MongoTemplate mongoTemplate;

  public MongoDatabase(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void saveInvoice(Invoice invoice) {
    if (invoice == null) {
      String message = "Invoice cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    invoice.setId(getDatabaseId(invoice));
    mongoTemplate.save(invoice);
  }

  private Long getDatabaseId(Invoice invoice) {
    Long id;
    Query query = new Query();
    query.with(new Sort(Sort.Direction.DESC, "_id"));
    if (mongoTemplate.find(query, Invoice.class).isEmpty()) {
      id = 1L;
    } else {
      id = mongoTemplate.find(query, Invoice.class).get(0).getId();
      id++;
    }
    return id;
  }

  @Override
  public Invoice getInvoiceById(Long id) {
    return mongoTemplate.findById(String.valueOf(id), Invoice.class);
  }

  @Override
  public Collection<Invoice> getInvoices() {
    return mongoTemplate.findAll(Invoice.class);
  }

  @Override
  public boolean updateInvoice(Long id, Invoice invoice) {
    if (invoice == null) {
      String message = "Invoice cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").is(id));
    if (mongoTemplate.exists(query, Invoice.class)) {
      invoice.setId(id);
      mongoTemplate.save(invoice);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeInvoiceById(Long id) {
    Query query = new Query();
    query.addCriteria(Criteria.where("_id").is(id));
    if (mongoTemplate.exists(query, Invoice.class)) {
      mongoTemplate.remove(query, Invoice.class);
      return true;
    }
    return false;
  }
}
