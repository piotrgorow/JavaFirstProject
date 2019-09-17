package pl.coderstrust.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderstrust.accounting.util.json.InvoiceJsonConverter;
import pl.coderstrust.database.InvoiceTestUtil;
import pl.coderstrust.model.Invoice;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
@TestPropertySource(locations = "classpath:integration-test.properites")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class InvoiceIntegrationTest {

  @Autowired
  private InvoiceJsonConverter invoiceJsonConverter;

  @Autowired
  private MockMvc mvc;

  private void addInvoice(Invoice invoice) throws Exception {
    mvc.perform(post("/invoices")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invoiceJsonConverter.toJson(invoice)));
  }

  @Test
  @DisplayName("Should return all invoices")
  void shouldReturnAllInvoices() throws Exception {
    // Given
    List<Invoice> invoices = new ArrayList<>(
        Arrays.asList(InvoiceTestUtil.sampleInvoiceFromFile(), InvoiceTestUtil.sampleInvoiceFromFile2()));
    addInvoice(InvoiceTestUtil.sampleInvoiceFromFile());
    addInvoice(InvoiceTestUtil.sampleInvoiceFromFile2());

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
    addInvoice(invoice);

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
    addInvoice(invoice);

    // Then
    mvc.perform(post("/invoices")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invoiceJsonConverter.toJson(invoice)))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("Should update invoice")
  void shouldUpdateInvoice() throws Exception {
    // Given
    Long invoiceId = 1L;
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();
    String invoiceAsJson = invoiceJsonConverter.toJson(invoice).replace("inv1", "inv2");
    addInvoice(invoiceJsonConverter.fromJson(invoiceAsJson));

    // Then
    mvc.perform(put(String.format("/invoices/%d", invoiceId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(invoiceAsJson))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should delete invoice")
  void shouldDeleteInvoice() throws Exception {
    // Given
    Long invoiceId = 1L;
    addInvoice(InvoiceTestUtil.sampleInvoiceFromFile());

    // Then
    mvc.perform(delete(String.format("/invoices/%d", invoiceId)))
        .andExpect(status().isOk());
  }
}
