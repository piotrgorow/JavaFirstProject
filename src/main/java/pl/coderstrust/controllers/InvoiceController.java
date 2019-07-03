package pl.coderstrust.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.coderstrust.accounting.util.json.InvoiceJsonConverter;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.InvoiceService;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

  private final InvoiceService invoiceService;
  private final InvoiceJsonConverter invoiceJsonConverter;

  @Autowired
  InvoiceController(InvoiceService invoiceService, InvoiceJsonConverter invoiceJsonConverter) {
    this.invoiceService = invoiceService;
    this.invoiceJsonConverter = invoiceJsonConverter;
  }

  @PostMapping
  public ResponseEntity<?> addInvoice(@RequestBody Invoice invoice) {
    try {
      invoiceService.saveInvoice(invoice);
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.setLocation(URI.create(String.format("/invoices/%d", invoice.getId())));
      return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping
  public ResponseEntity<?> getInvoices() {
    try {
      Collection<Invoice> invoices = invoiceService.getInvoices();
      if (invoices == null) {
        return new ResponseEntity<>(new ArrayList<Invoice>(), HttpStatus.OK);
      }
      return new ResponseEntity<>(invoiceJsonConverter.toJsonAsList(invoices), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getInvoiceById(@PathVariable("id") Long id) {
    try {
      Invoice invoice = invoiceService.getInvoiceById(id);
      if (invoice == null) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(invoiceJsonConverter.toJson(invoice), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateInvoice(@PathVariable("id") Long id, @RequestBody Invoice invoice) {
    try {
      if (!id.equals(invoice.getId())) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
      if (!invoiceService.updateInvoice(id, invoice)) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> removeInvoiceById(@PathVariable("id") Long id) {
    try {
      if (!invoiceService.removeInvoiceById(id)) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
