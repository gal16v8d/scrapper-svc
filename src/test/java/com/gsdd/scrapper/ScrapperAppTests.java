package com.gsdd.scrapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = ScrapperApp.class)
class ScrapperAppTests {

  @Test
  void contextLoads(ApplicationContext context) {
    Assertions.assertNotNull(context);
  }
}
