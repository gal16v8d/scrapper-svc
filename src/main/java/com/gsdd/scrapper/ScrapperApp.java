package com.gsdd.scrapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class ScrapperApp {

  static {
    // This ensures the property is set when the class is loaded
    // and before any Jsoup operations that require HTTP/2 support.
    System.setProperty("jsoup.useHttpClient", "true");
  }

  public static void main(String[] args) {
    SpringApplication.run(ScrapperApp.class, args);
  }
}
