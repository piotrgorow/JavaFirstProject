package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class FileHelperTest {

  @Test
  @DisplayName("Should throw an exception when the file path is null")
  void shouldThrowExceptionWhenFilePathIsNull() {
    FileHelper fileHelper = new FileHelper();
    assertThrows(IllegalArgumentException.class, () -> fileHelper.readLines(null));
  }

  @Test
  @DisplayName("Should throw an exception when the file is not find")
  void shouldThrowExceptionWhenFileNotFound() {
    FileHelper fileHelper = new FileHelper();
    assertThrows(FileNotFoundException.class, () -> fileHelper.readLines("src/main/resources/test.txt"));
  }

  @Test
  @DisplayName("Should write throw an exception when the file is not find")
  void shouldWriteThrowExceptionWhenFileNotFound() {
    //given
    FileHelper fileHelper = new FileHelper();
    List<String> resultLines = new ArrayList<>();
    resultLines.add("23");
    //then
    assertThrows(FileNotFoundException.class,
        () -> fileHelper.writeLines(resultLines, "src/main/resources/test1.txt"));
  }

  @Test
  @DisplayName("Should read line from file")
  void shouldReadLinesFromFile() throws IOException {
    //given
    FileHelper fileHelper = new FileHelper();
    String inputFile = "src/test/resources/input.txt";
    List<String> resultLines = new ArrayList<>();
    resultLines.add("12345");
    resultLines.add("12345");

    //when
    List<String> actual;
    actual = fileHelper.readLines(inputFile);

    //then
    assertEquals(resultLines, actual);
  }

  @Test
  @DisplayName("Should write line to file")
  void writeLinesToFile() throws IOException {
    //given
    FileHelper fileHelper = new FileHelper();
    String outputFile = "src/test/resources/output.txt";
    String expectedFile = "src/test/resources/expected.txt";
    List<String> resultLines = new ArrayList<>();
    resultLines.add("12345");

    //when
    fileHelper.writeLines(resultLines, outputFile);
    byte[] expected = Files.readAllBytes(Paths.get(expectedFile));
    byte[] actual = Files.readAllBytes(Paths.get(outputFile));

    //then
    assertArrayEquals(expected, actual);
  }
}
