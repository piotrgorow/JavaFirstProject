package pl.coderstrust.controllers;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.coderstrust.accounting.util.json.InvoiceJsonConverter;
import pl.coderstrust.database.InvoiceTestUtil;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.InvoiceService;

@ExtendWith(SpringExtension.class)
class InvoiceControllerTest {

  private final InvoiceJsonConverter invoiceJsonConverter = new InvoiceJsonConverter();
  private MockMvc mvc;

  @MockBean
  private InvoiceService invoiceService;

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
    when(invoiceService.getInvoices()).thenReturn(invoices);

    // When
    mvc.perform(get("/invoices"))
        .andExpect(status().isOk())
        .andExpect(content().json(invoiceJsonConverter.toJsonAsList(invoices)));

    // Then
    verify(invoiceService).getInvoices();
  }

  @Test
  @DisplayName("Should return empty ArrayList when list of invoices is null")
  void shouldReturnEmptyArrayListWhenListOfInvoicesIsNull() throws Exception {
    // Given
    when(invoiceService.getInvoices()).thenReturn(null);

    // When
    mvc.perform(get("/invoices"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    // Then
    verify(invoiceService).getInvoices();
  }

  @Test
  @DisplayName("Should return one invoice")
  void shouldReturnOneInvoice() throws Exception {
    // Given
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();
    Long invoiceId = invoice.getId();
    when(invoiceService.getInvoiceById(invoiceId)).thenReturn(invoice);

    // When
    mvc.perform(get(String.format("/invoices/%d", invoiceId)))
        .andExpect(status().isOk())
        .andExpect(content().json(invoiceJsonConverter.toJson(invoice)));

    // Then
    verify(invoiceService).getInvoiceById(invoiceId);
  }

  @Test
  @DisplayName("Should return not found status when invoice not exist")
  void shouldReturnNotFoundWhenInvoiceNotExist() throws Exception {
    // Given
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();
    Long invoiceId = invoice.getId();
    when(invoiceService.getInvoiceById(invoiceId)).thenReturn(null);

    // When
    mvc.perform(get(String.format("/invoices/%d", invoiceId)))
        .andExpect(status().isNotFound());

    // Then
    verify(invoiceService).getInvoiceById(invoiceId);
  }

  @Test
  @DisplayName("Should delete invoice")
  void shouldDeleteInvoice() throws Exception {
    // Given
    Long invoiceId = 1L;
    when(invoiceService.removeInvoiceById(invoiceId)).thenReturn(true);

    // When
    mvc.perform(delete(String.format("/invoices/%d", invoiceId)))
        .andExpect(status().isOk());

    // Then
    verify(invoiceService).removeInvoiceById(invoiceId);
  }

  @Test
  @DisplayName("Should return not found status when deleted invoice not exist in database")
  void shouldReturnNotFoundWhenDeletedInvoiceNotExistInDatabase() throws Exception {
    // Given
    Long invoiceId = 3L;
    when(invoiceService.removeInvoiceById(invoiceId)).thenReturn(false);

    // When
    mvc.perform(delete(String.format("/invoices/%d", invoiceId)))
        .andExpect(status().isNotFound());

    // Then
    verify(invoiceService).removeInvoiceById(invoiceId);
  }

  @Test
  @DisplayName("Should add invoice")
  void shouldAddInvoice() throws Exception {
    // Given
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();
    doNothing().when(invoiceService).saveInvoice(isA(Invoice.class));

    // When
    mvc.perform(post("/invoices")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invoiceJsonConverter.toJson(invoice)))
        .andExpect(status().isCreated());

    // Then
    verify(invoiceService).saveInvoice(isA(Invoice.class));
  }

  @Test
  @DisplayName("Should update invoice")
  void shouldUpdateInvoice() throws Exception {
    // Given
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();
    String invoiceAsJson = invoiceJsonConverter.toJson(invoice);
    Long invoiceId = invoice.getId();
    when(invoiceService.updateInvoice(invoiceId, invoice)).thenReturn(true);

    // When
    mvc.perform(put(String.format("/invoices/%d", invoiceId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(invoiceAsJson))
        .andExpect(status().isOk());

    // Then
    verify(invoiceService).updateInvoice(invoiceId, invoice);
  }

  @Test
  @DisplayName("Should return not found status when updated invoice not exists in database")
  void shouldReturnNotFoundWhenUpdatedInvoiceNotExistsInDatabase() throws Exception {
    // Given
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();
    Long invoiceId = invoice.getId();
    when(invoiceService.updateInvoice(invoiceId, invoice)).thenReturn(false);

    // When
    mvc.perform(put(String.format("/invoices/%d", invoiceId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(invoiceJsonConverter.toJson(invoice)))
        .andExpect(status().isNotFound());

    // Then
    verify(invoiceService).updateInvoice(invoiceId, invoice);
  }

  @Test
  @DisplayName("Should return bad request when updated invoice ID and passed ID are different")
  void shouldReturnBadRequestWhenUpdatedInvoiceIdAndPassedIdAreDifferent() throws Exception {
    // Given
    Invoice invoice = InvoiceTestUtil.sampleInvoiceFromFile();
    Invoice invoice2 = InvoiceTestUtil.sampleInvoiceFromFile2();
    Long invoiceId = invoice.getId();
    when(invoiceService.updateInvoice(invoiceId, invoice)).thenReturn(true);

    // Then
    mvc.perform(put(String.format("/invoices/%d", invoiceId))
        .contentType(MediaType.APPLICATION_JSON)
        .content(invoiceJsonConverter.toJson(invoice2)))
        .andExpect(status().isBadRequest());
  }
}
