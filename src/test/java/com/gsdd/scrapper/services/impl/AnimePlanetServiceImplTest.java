package com.gsdd.scrapper.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.spy;

import com.gsdd.scrapper.properties.MediaProperties;
import com.gsdd.scrapper.services.AnimePlanetService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

@ExtendWith(MockitoExtension.class)
class AnimePlanetServiceImplTest {

  private static final String MOCK_URL = "http://dummy-page.net";
  private static final String MOCK_BASIC_HTML = "<html><body><h1>Mocked HTML</h1></body></html>";
  @Mock
  private MediaProperties mediaProperties;
  @Mock
  private Connection connection;
  private AnimePlanetService service;

  @BeforeEach
  void setUp() {
    service = spy(new AnimePlanetServiceImpl(mediaProperties, connection));
  }

  @Test
  void testGetMainPageInfo() throws IOException {
    willReturn(MOCK_URL).given(mediaProperties).getAnimePlanetUrl();
    willReturn(connection).given(connection).url(anyString());
    willReturn(connection).given(connection).headers(any());
    willReturn(connection).given(connection).userAgent(any());
    willReturn(Jsoup.parse(MOCK_BASIC_HTML)).given(connection).get();
    Document doc = service.getMainPageInfo();
    Assertions.assertNotNull(doc);
  }

  @Test
  void testGetPageInfo() throws IOException {
    willReturn(MOCK_URL).given(mediaProperties).getAnimePlanetUrl();
    willReturn(connection).given(connection).url(anyString());
    willReturn(connection).given(connection).data(anyString(), anyString());
    willReturn(connection).given(connection).headers(any());
    willReturn(connection).given(connection).userAgent(any());
    willReturn(Jsoup.parse(MOCK_BASIC_HTML)).given(connection).get();
    Document doc = service.getPageInfo(0);
    Assertions.assertNotNull(doc);
  }

  @Test
  void testExtractAnimeInfo() throws IOException {
    willReturn(readDocument("anime-planet-main.html")).given(service).getPageInfo(anyInt());
    willReturn(MOCK_URL).given(mediaProperties).getAnimePlanetUrl();
    willReturn(connection).given(connection).url(anyString());
    willReturn(connection).given(connection).headers(any());
    willReturn(connection).given(connection).userAgent(any());
    willReturn(readDocument("anime-planet-detail.html")).given(connection).get();
    var result = service.extractAnimeInfo(0);
    Assertions.assertNotNull(result);
  }

  private Document readDocument(String name) throws IOException {
    ClassPathResource resource = new ClassPathResource(name);
    byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
    String doc = new String(bytes, StandardCharsets.UTF_8);
    return Jsoup.parse(doc);
  }
}
