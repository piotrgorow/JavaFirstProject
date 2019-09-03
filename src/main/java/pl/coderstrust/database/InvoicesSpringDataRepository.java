package pl.coderstrust.database;

import org.springframework.data.repository.CrudRepository;
import pl.coderstrust.model.Invoice;

interface InvoicesSpringDataRepository extends CrudRepository<Invoice, Long> {

}
