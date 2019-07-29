package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.accounting.util.json.InvoiceJsonConverter;
import pl.coderstrust.configuration.InFileDatabaseProperties;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
@DisplayName("InFileDatabase Test")
class InFileDatabaseTest {

  @Mock
  FileHelper fileHelper;
  @Mock
  InvoiceJsonConverter invoiceJsonConverter;
  private InFileDatabase inFileDatabase;

  private InFileDatabaseProperties properties;

  @BeforeEach
  void setup() throws IOException {
    properties = new InFileDatabaseProperties();
    properties.setFilePath("src/test/resources/file.txt");
    doReturn("").when(fileHelper).getLastLine(properties.getFilePath());
    doNothing().when(fileHelper).checkFilesExistence((properties.getFilePath()));
    inFileDatabase = new InFileDatabase(fileHelper, invoiceJsonConverter, properties);
  }

  @Test
  @DisplayName("Should process provided invoice and save it to the output file")
  void shouldProcessProvidedInvoiceAndSaveItToTheOutputFIle() throws IOException {
    // Given
    Invoice invoiceToSave = InvoiceTestUtil.sampleInvoice();
    String invoiceToSaveAsJson = "{\"id\":1,\"invoiceNumber\":\"inv1\",\"date\":\"2019-01-01\","
        + "\"seller\":{\"name\":\"XYZ\",\"taxIdentificationNumber\":\"11111\",\"address\":"
        + "\"London\"},\"buyer\":{\"name\":\"QAZ\",\"taxIdentificationNumber\":\"22222\","
        + "\"address\":\"Chicago\"},\"invoiceEntries\":[{\"description\":\"Kiwi\","
        + "\"quantity\":10,\"value\":125.23,\"vatRate\":\"VAT_23\",\"vatValue\":28.8029},"
        + "{\"description\":\"Jablka\",\"quantity\":100,\"value\":1.99,\"vatRate\":\"VAT_8\","
        + "\"vatValue\":0.1592},{\"description\":\"Pomarancze\",\"quantity\":254,\"value\":99.0,"
        + "\"vatRate\":\"VAT_0\",\"vatValue\":0.00}]}";
    doReturn(invoiceToSaveAsJson).when(invoiceJsonConverter).toJson(invoiceToSave);

    // When
    inFileDatabase.saveInvoice(invoiceToSave);

    // Then
    verify(invoiceJsonConverter).toJson(invoiceToSave);
    verify(fileHelper).getLastLine(properties.getFilePath());
    verify(fileHelper).checkFilesExistence(properties.getFilePath());
  }

  @Test
  @DisplayName("Should throw exception if the invoice is null")
  void shouldThrowExceptionIfInvoiceIsNull() {
    assertThrows(IllegalArgumentException.class, () -> inFileDatabase.saveInvoice(null));
  }

  @Test
  @DisplayName("Should return correct invoice by ID")
  void shouldReturnCorrectInvoiceById() throws IOException {
    // Given
    String firstLine = InvoiceTestUtil.sampleInvoiceJson;
    String secondLine = InvoiceTestUtil.sampleInvoiceJson2;
    String thirdLine = InvoiceTestUtil.sampleInvoiceJson3;
    Invoice firstInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile();
    Invoice secondInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile2();
    Invoice thirdInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile3();
    doReturn(Arrays.asList(firstLine, secondLine, thirdLine)).when(fileHelper)
        .readLines(properties.getFilePath());
    doReturn(firstInvoiceFromFile).when(invoiceJsonConverter).fromJson(firstLine);
    doReturn(secondInvoiceFromFile).when(invoiceJsonConverter).fromJson(secondLine);
    doReturn(thirdInvoiceFromFile).when(invoiceJsonConverter).fromJson(thirdLine);
    Invoice expected = InvoiceTestUtil.sampleInvoiceFromFile();

    // When
    Invoice result = inFileDatabase.getInvoiceById(1L);

    // Then
    assertEquals(expected, result);
    verify(fileHelper).readLines(properties.getFilePath());
    verify(invoiceJsonConverter).fromJson(firstLine);
    verify(invoiceJsonConverter).fromJson(secondLine);
    verify(invoiceJsonConverter).fromJson(thirdLine);
  }

  @Test
  @DisplayName("Should return null for invoice which does not exists")
  void shouldReturnNullForIdWhichDoesNotExists() throws IOException {
    // Given
    String firstLine = InvoiceTestUtil.sampleInvoiceJson;
    String secondLine = InvoiceTestUtil.sampleInvoiceJson2;
    String thirdLine = InvoiceTestUtil.sampleInvoiceJson3;
    Invoice firstInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile();
    Invoice secondInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile2();
    Invoice thirdInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile3();
    doReturn(Arrays.asList(firstLine, secondLine, thirdLine)).when(fileHelper)
        .readLines(properties.getFilePath());
    doReturn(firstInvoiceFromFile).when(invoiceJsonConverter).fromJson(firstLine);
    doReturn(secondInvoiceFromFile).when(invoiceJsonConverter).fromJson(secondLine);
    doReturn(thirdInvoiceFromFile).when(invoiceJsonConverter).fromJson(thirdLine);

    // When
    Invoice result = inFileDatabase.getInvoiceById(10L);

    // Then
    assertNull(result);
    verify(fileHelper).readLines(properties.getFilePath());
    verify(invoiceJsonConverter).fromJson(firstLine);
    verify(invoiceJsonConverter).fromJson(secondLine);
    verify(invoiceJsonConverter).fromJson(thirdLine);
  }

  @Test
  @DisplayName("Should return all invoices in the file")
  void shouldReturnAllInvoices() throws IOException {
    // Given
    String firstLine = InvoiceTestUtil.sampleInvoiceJson;
    String secondLine = InvoiceTestUtil.sampleInvoiceJson2;
    String thirdLine = InvoiceTestUtil.sampleInvoiceJson3;
    Invoice firstInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile();
    Invoice secondInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile2();
    Invoice thirdInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile3();
    doReturn(Arrays.asList(firstLine, secondLine, thirdLine)).when(fileHelper)
        .readLines(properties.getFilePath());
    doReturn(firstInvoiceFromFile).when(invoiceJsonConverter).fromJson(firstLine);
    doReturn(secondInvoiceFromFile).when(invoiceJsonConverter).fromJson(secondLine);
    doReturn(thirdInvoiceFromFile).when(invoiceJsonConverter).fromJson(thirdLine);

    Set<Invoice> expected = new HashSet<>();
    expected.add(firstInvoiceFromFile);
    expected.add(secondInvoiceFromFile);
    expected.add(thirdInvoiceFromFile);

    // When
    Collection<Invoice> result = inFileDatabase.getInvoices();

    // Then
    for (Invoice invoice : result) {
      assertTrue(expected.contains(invoice));
    }
    verify(fileHelper).readLines(properties.getFilePath());
    verify(invoiceJsonConverter).fromJson(firstLine);
    verify(invoiceJsonConverter).fromJson(secondLine);
    verify(invoiceJsonConverter).fromJson(thirdLine);
  }

  @Test
  @DisplayName("Should return empty collection for no invoices in file")
  void shouldReturnEmptyCollectionForNoInvoiceInFile() throws IOException {
    // Given
    doReturn(new ArrayList<>()).when(fileHelper).readLines(properties.getFilePath());

    // When
    Collection<Invoice> result = inFileDatabase.getInvoices();

    // Then
    assertTrue(result.isEmpty());
    verify(fileHelper).readLines(properties.getFilePath());
  }

  @Test
  @DisplayName("Should throw exception when parameter invoice is null")
  void shouldThrowExceptionWhenParameterInvoiceIsNull() {
    assertThrows(IllegalArgumentException.class, () -> inFileDatabase.updateInvoice(1L, null));
  }

  @Test
  @DisplayName("Should correct update invoice")
  void shouldCorrectUpdateInvoice() throws IOException {
    // Given
    String firstLine = InvoiceTestUtil.sampleInvoiceJson;
    Invoice firstInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile();
    doReturn(Collections.singletonList(firstLine)).when(fileHelper)
        .readLines(properties.getFilePath());
    doReturn(firstInvoiceFromFile).when(invoiceJsonConverter).fromJson(firstLine);
    // When
    boolean result = inFileDatabase.updateInvoice(1L, InvoiceTestUtil.sampleInvoice());
    // Then
    assertTrue(result);
    verify(fileHelper).readLines(properties.getFilePath());
    verify(invoiceJsonConverter).fromJson(firstLine);
  }

  @Test
  @DisplayName("Should correct delete invoice")
  void shouldCorrectDeleteInvoice() throws IOException {
    // Given
    String firstLine = InvoiceTestUtil.sampleInvoiceJson;
    Invoice firstInvoiceFromFile = InvoiceTestUtil.sampleInvoiceFromFile();
    doReturn(Collections.singletonList(firstLine)).when(fileHelper)
        .readLines(properties.getFilePath());
    doReturn(firstInvoiceFromFile).when(invoiceJsonConverter).fromJson(firstLine);
    // When
    boolean result = inFileDatabase.removeInvoiceById(1L);
    // Then
    assertTrue(result);
    verify(fileHelper).readLines(properties.getFilePath());
    verify(invoiceJsonConverter).fromJson(firstLine);
  }
}
