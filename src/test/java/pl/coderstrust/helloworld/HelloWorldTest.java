package pl.coderstrust.helloworld;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HelloWorldTest {

  @Test
  void shouldReturnHelloWorld() {
    //given
    HelloWorld helloWorld = new HelloWorld();
    String expected = "Hello World!";

    //when
    String result = helloWorld.getHelloWorld();

    //then
    assertEquals(expected, result);
  }
}
