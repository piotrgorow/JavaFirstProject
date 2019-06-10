package pl.coderstrust.helloworld;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HelloWorldTest {

  @Test
  void shouldReturnHelloWorld() {
    //given
    String expected = "Hello World!";

    //when
    String result = HelloWorld.getHelloWorld();

    //then
    assertEquals(expected, result);
  }
}
