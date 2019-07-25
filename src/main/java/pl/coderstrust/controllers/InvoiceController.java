package pl.coderstrust.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "Invoices")
public class InvoiceController {

  private final InvoiceService invoiceService;
  private final InvoiceJsonConverter invoiceJsonConverter;

  @Autowired
  InvoiceController(InvoiceService invoiceService, InvoiceJsonConverter invoiceJsonConverter) {
    this.invoiceService = invoiceService;
    this.invoiceJsonConverter = invoiceJsonConverter;
  }

  @PostMapping
  @ApiOperation(value = "Add invoice")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 201, message = "Created"),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 405, message = "Method Not Allowed"),
      @ApiResponse(code = 500, message = "Failure")})
  public ResponseEntity<?> addInvoice(
      @ApiParam(value = "invoice", type = "Invoice", required = true)
      @RequestBody Invoice invoice) {
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
  @ApiOperation(value = "Get all invoices")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 500, message = "Failure")})
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
  @ApiOperation(value = "Get invoice", notes = "Get the invoice with the given ID")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 405, message = "Method Not Allowed"),
      @ApiResponse(code = 500, message = "Failure")})
  public ResponseEntity<?> getInvoiceById(
      @ApiParam(value = "identifier", example = "1", type = "Long", required = true)
      @PathVariable("id") Long id) {
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
  @ApiOperation(value = "Update invoice", notes = "Modify the invoice with the given ID")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 204, message = "No Content"),
      @ApiResponse(code = 400, message = "Bad Request"),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 405, message = "Method Not Allowed"),
      @ApiResponse(code = 500, message = "Failure")})
  public ResponseEntity<?> updateInvoice(
      @ApiParam(value = "identifier", example = "1", type = "Long", required = true)
      @PathVariable("id") Long id, @RequestBody Invoice invoice) {
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
  @ApiOperation(value = "Delete invoice", notes = "Delete the invoice with the given ID")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Success"),
      @ApiResponse(code = 404, message = "Not Found"),
      @ApiResponse(code = 405, message = "Method Not Allowed"),
      @ApiResponse(code = 500, message = "Failure")})
  public ResponseEntity<?> removeInvoiceById(
      @ApiParam(value = "identifier", example = "1", type = "Long", required = true)
      @PathVariable("id") Long id) {
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
