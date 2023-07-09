package com.gsdd.scrapper.config;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionConfig {

  @Bean
  Connection getConnection() {
    return Jsoup.newSession();
  }
}
