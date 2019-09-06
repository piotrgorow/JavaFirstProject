package pl.coderstrust.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "in-file")
@Slf4j
class FileHelper {

  List<String> readLines(String filePath) throws IOException {
    if (filePath == null) {
      String message = "File path cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    List<String> resultLines = new ArrayList<>();
    try (FileReader fileReader = new FileReader(filePath)) {
      try (Scanner scannerLine = new Scanner(fileReader)) {
        log.info("Reading lines from a file");
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
      String message = "Lines cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    if (filePath == null) {
      String message = "File path cannot be null";
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    try (FileWriter fileWriter = new FileWriter(filePath, append)) {
      try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
        try (PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
          log.info("Writing lines to a file");
          for (String line : lines) {
            printWriter.println(line);
          }
        }
      }
    }
  }

  void checkFilesExistence(String filePath) throws IOException {
    File invoiceFile = new File(filePath);
    log.info("Checking if the file exist");
    if (!invoiceFile.exists()) {
      log.info("File {} exist", filePath);
      PrintWriter printWriter = new PrintWriter(filePath);
    }
  }

  String getLastLine(String path) throws IOException {
    File file = new File(path);
    ReversedLinesFileReader reversedLinesFileReader = new ReversedLinesFileReader(file, Charset.defaultCharset());
    log.info("Reading last line from a file");
    return reversedLinesFileReader.readLine();
  }
}
