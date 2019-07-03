package pl.coderstrust.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.io.input.ReversedLinesFileReader;
import pl.coderstrust.configuration.InvoiceConfig;

public class FileHelper {

  List<String> readLines(String filePath) throws IOException {
    if (filePath == null) {
      throw new IllegalArgumentException("File path cannot be null");
    }
    List<String> resultLines = new ArrayList<>();
    try (FileReader fileReader = new FileReader(filePath)) {
      try (Scanner scannerLine = new Scanner(fileReader)) {
        while (scannerLine.hasNextLine()) {
          String line = scannerLine.nextLine();
          resultLines.add(line);
        }
      }
    }
    return resultLines;
  }

  void writeLines(List<String> lines, String filePath, boolean append) throws IOException {
    if (lines == null) {
      throw new IllegalArgumentException("Lines cannot be null");
    }
    if (filePath == null) {
      throw new IllegalArgumentException("File path cannot be null");
    }
    try (FileWriter fileWriter = new FileWriter(filePath, append)) {
      try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
        try (PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
          for (String line : lines) {
            printWriter.println(line);
          }
        }
      }
    }
  }

  void checkFilesExistence() throws IOException {
    File invoiceFile = new File(InvoiceConfig.INVOICE_DATABASE_FILE);
    if (!invoiceFile.exists()) {
      PrintWriter printWriter = new PrintWriter(InvoiceConfig.INVOICE_DATABASE_FILE);
    }
  }

  String getLastLine(String path) throws IOException {
    File file = new File(path);
    ReversedLinesFileReader reversedLinesFileReader = new ReversedLinesFileReader(file);
    return reversedLinesFileReader.readLine();
  }
}
