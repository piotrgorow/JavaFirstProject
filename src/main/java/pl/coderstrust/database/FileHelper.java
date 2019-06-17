package pl.coderstrust.database;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class FileHelper {

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

  void writeLines(List<String> lines, String filePath) throws FileNotFoundException {
    if (lines == null) {
      throw new IllegalArgumentException("Lines cannot be null");
    }
    if (filePath == null) {
      throw new IllegalArgumentException("File path cannot be null");
    }
    try (PrintWriter printWriter = new PrintWriter(filePath)) {
      for (String line : lines) {
        printWriter.println(line);
      }
    }
  }
}