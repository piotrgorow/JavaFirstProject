package pl.coderstrust.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.stream.Stream;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import pl.coderstrust.database.InvoiceTestUtil;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.services.EmailService.Operation;

@ExtendWith(MockitoExtension.class)
@DisplayName("Email Service test")
class EmailServiceTest {

  @Mock
  private JavaMailSender sender;
  @InjectMocks
  private EmailService emailService = new EmailService(sender, "email.invoices.service@gmail.com", true);

  @Test
  @DisplayName("Should send a valid email")
  void shouldSendValidEmail() throws MessagingException {
    // Given
    MimeMessage message = new MimeMessage((Session) null);
    when(sender.createMimeMessage()).thenReturn(message);
    Invoice invoice = InvoiceTestUtil.sampleInvoice();
    doNothing().when(sender).send(message);

    // When
    emailService.sendInvoiceCreatedMail(invoice);

    // Then
    ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(sender, times(1)).send((mimeMessageArgumentCaptor.capture()));
    assertEquals("New Invoice has been added.", mimeMessageArgumentCaptor.getValue().getSubject());
  }

  @ParameterizedTest
  @MethodSource("reportParameters")
  @DisplayName("Should correctly add item to the report")
  void shouldCorrectlyAddToTheReport(Operation operation, String expected) {
    // When
    emailService.addToReport(operation, expected);
    String result = emailService.getReport().get(operation).get(0);

    // Then
    assertEquals(expected, result);
  }

  private static Stream<Arguments> reportParameters() {
    return Stream.of(
        Arguments.of(Operation.INVOICE_ADDED, "INV 1/01/2019"),
        Arguments.of(Operation.INVOICE_MODIFIED, "INV 2/02/2019"),
        Arguments.of(Operation.INVOICE_REMOVED, "INV 3/03/2019")
    );
  }

  @Test
  @DisplayName("Should send a valid email with the report")
  void shouldSendValidEmailWithTheReport() throws MessagingException, IOException {
    // Given
    emailService.addToReport(Operation.INVOICE_ADDED, "INV 1/01/2019");
    emailService.addToReport(Operation.INVOICE_ADDED, "INV 2/02/2019");
    emailService.addToReport(Operation.INVOICE_ADDED, "INV 3/03/2019");
    emailService.addToReport(Operation.INVOICE_MODIFIED, "INV 2/02/2019");
    emailService.addToReport(Operation.INVOICE_MODIFIED, "INV 1/01/2019");
    emailService.addToReport(Operation.INVOICE_REMOVED, "3");
    emailService.addToReport(Operation.INVOICE_REMOVED, "2");
    emailService.addToReport(Operation.INVOICE_REMOVED, "1");
    MimeMessage message = new MimeMessage((Session) null);
    when(sender.createMimeMessage()).thenReturn(message);
    doNothing().when(sender).send(message);
    String expected = "\nReport of invoices - date: " + LocalDate.now().minusDays(1L) + "\n\nAdded "
        + "invoices:\nINV 1/01/2019\nINV 2/02/2019\nINV 3/03/2019\n\nModified invoices:\nINV 2/02/2019\n"
        + "INV 1/01/2019\n\nRemoved invoices:\n3\n2\n1\n";

    // When
    emailService.sendReport();

    // Then
    ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(sender, times(1)).send((mimeMessageArgumentCaptor.capture()));
    String messageText = streamToString(mimeMessageArgumentCaptor.getValue().getInputStream());
    assertEquals(expected, messageText);
    assertEquals("Daily report of invoices.", mimeMessageArgumentCaptor.getValue().getSubject());
  }

  @Test
  @DisplayName("Should send a valid email with a blank report")
  void shouldSendValidEmailWithBlankReport() throws MessagingException, IOException {
    // Given
    MimeMessage message = new MimeMessage((Session) null);
    when(sender.createMimeMessage()).thenReturn(message);
    doNothing().when(sender).send(message);
    String expected = "\nReport of invoices - date: " + LocalDate.now().minusDays(1L) + "\n\nAdded invoices:\nNo"
        + " invoices.\n\nModified invoices:\nNo invoices.\n\nRemoved invoices:\nNo invoices.\n";

    // When
    emailService.sendReport();

    // Then
    ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(sender, times(1)).send((mimeMessageArgumentCaptor.capture()));
    String messageText = streamToString(mimeMessageArgumentCaptor.getValue().getInputStream());
    assertEquals(expected, messageText);
    assertEquals("Daily report of invoices.", mimeMessageArgumentCaptor.getValue().getSubject());
  }

  private String streamToString(InputStream inputStream) throws IOException {
    StringBuilder textBuilder = new StringBuilder();
    try (Reader reader = new BufferedReader(new InputStreamReader(inputStream,
        Charset.forName(StandardCharsets.UTF_8.name())))) {
      int c;
      while ((c = reader.read()) != -1) {
        textBuilder.append((char) c);
      }
    }
    return textBuilder.toString();
  }
}
