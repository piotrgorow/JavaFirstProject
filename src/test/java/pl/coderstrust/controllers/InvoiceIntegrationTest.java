package pl.coderstrust.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.coderstrust.accounting.util.json.InvoiceJsonConverter;
import pl.coderstrust.database.InMemoryDatabase;
import pl.coderstrust.database.InvoiceTestUtil;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.InvoiceService;

class InvoiceIntegrationTest {

  private final InvoiceJsonConverter invoiceJsonConverter = new InvoiceJsonConverter();
  private final InvoiceService invoiceService = new InvoiceService(new InMemoryDatabase());
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    mvc = MockMvcBuilders.standaloneSetup(new InvoiceController(invoiceService, invoiceJsonConverter)).build();
  }

  @Test
  @DisplayName("Should return all invoices")
  void shouldReturnAllInvoices() throws Exception {
    // Given
    List<Invoice> invoices = Arrays
        .asList(InvoiceTestUtil.sampleInvoiceFromFile(), InvoiceTestUtil.sampleInvoiceFromFile2());
    invoiceService.saveInvoice(invoices.get(0));
    invoiceService.saveInvoice(invoices.get(1));

    // Then
    mvc.perform(get("/invoices"))
        .andExpect(status().isOk())
        .andExpect(content().json(invoiceJsonConverter.toJsonAsList(invoices)));
  }

  @Test
  @DisplayName("Should return one invoice")
  void shouldReturnOneInvoice() throws Exception {
    // Given
    Long invoiceId = 1L;
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();
    invoiceService.saveInvoice(invoice);

    // Then
    mvc.perform(get(String.format("/invoices/%d", invoiceId)))
        .andExpect(status().isOk())
        .andExpect(content().json(invoiceJsonConverter.toJson(invoice)));
  }

  @Test
  @DisplayName("Should add invoice")
  void shouldAddInvoice() throws Exception {
    // Given
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();

    // When
    mvc.perform(post("/invoices")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invoiceJsonConverter.toJson(invoice)))
        .andExpect(status().isCreated());
    Invoice result = invoiceService.getInvoiceById(1L);

    // Then
    assertEquals(invoice, result);
  }

  @Test
  @DisplayName("Should update invoice")
  void shouldUpdateInvoice() throws Exception {
    // Given
    Long invoiceId = 1L;
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();
    invoiceService.saveInvoice(invoice);
    String invoiceAsJson = invoiceJsonConverter.toJson(invoice).replace("inv1", "inv2");
    Invoice expected = invoiceJsonConverter.fromJson(invoiceAsJson);

    // When
    mvc.perform(put(String.format("/invoices/%d", invoiceId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(invoiceAsJson))
        .andExpect(status().isOk());
    Invoice result = invoiceService.getInvoiceById(1L);

    // Then
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Should delete invoice")
  void shouldDeleteInvoice() throws Exception {
    // Given
    Long invoiceId = 1L;
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();
    invoiceService.saveInvoice(invoice);

    // When
    mvc.perform(delete(String.format("/invoices/%d", invoiceId)))
        .andExpect(status().isOk());

    // Then
    assertNull(invoiceService.getInvoiceById(1L));
  }
}
