package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    assertThrows(FileNotFoundException.class,
        () -> fileHelper.readLines("src/main/resources/test.txt"));
  }

  @Test
  @DisplayName("Should read line from file")
  void shouldReadLinesFromFile() throws IOException {
    //Given
    FileHelper fileHelper = new FileHelper();
    String inputFile = "src/test/resources/input.txt";
    List<String> resultLines = new ArrayList<>();
    resultLines.add("12345");
    resultLines.add("12345");

    //When
    List<String> actual;
    actual = fileHelper.readLines(inputFile);

    //Then
    assertEquals(resultLines, actual);
  }

  @Test
  @DisplayName("Should write line to file")
  void writeLinesToFile() throws IOException {
    //Given
    FileHelper fileHelper = new FileHelper();
    String outputFile = "src/test/resources/output.txt";
    String expectedFile = "src/test/resources/expected.txt";
    List<String> resultLines = new ArrayList<>();
    resultLines.add("12345");

    //When
    fileHelper.writeLines(resultLines, outputFile, false);
    byte[] expected = Files.readAllBytes(Paths.get(expectedFile));
    byte[] actual = Files.readAllBytes(Paths.get(outputFile));

    //Then
    assertArrayEquals(expected, actual);
  }

  @Test
  @DisplayName("Should throw exception when parameter lines is null")
  void shouldThrowExceptionWhenParameterLinesIsNull() {
    FileHelper fileHelper = new FileHelper();
    assertThrows(IllegalArgumentException.class, () -> fileHelper.writeLines(null, "", true));
  }

  @Test
  @DisplayName("Should throw exception when parameter filePath is null")
  void shouldThrowExceptionWhenParameterFilePathIsNull() {
    FileHelper fileHelper = new FileHelper();
    assertThrows(IllegalArgumentException.class,
        () -> fileHelper.writeLines(new ArrayList<>(), null, true));
  }

  @Test
  @DisplayName("Should return correct last line from file")
  void shouldReturnCorrectLastLineFromFile() throws IOException {
    // Given
    FileHelper fileHelper = new FileHelper();
    String path = "src/test/resources/input.txt";
    String expected = "12345";
    // When
    String result = fileHelper.getLastLine(path);
    // Then
    assertEquals(expected, result);
  }
}
