package com.gsdd.scrapper.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.spy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsdd.scrapper.properties.MediaProperties;
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
class YoutubeServiceImplTest {

  private static final String MOCK_BASIC_HTML = "<html><body><h1>Mocked HTML</h1></body></html>";
  private static final String MOCK_KEYWORD = "Test";
  private static final String MOCK_URL = "http://dummy-page.net";
  private static final ObjectMapper MAPPER = new ObjectMapper();
  @Mock
  private MediaProperties mediaProperties;
  @Mock
  private Connection connection;
  private YoutubeServiceImpl service;

  @BeforeEach
  void setUp() {
    service = spy(new YoutubeServiceImpl(mediaProperties, connection, MAPPER));
  }

  @Test
  void testGetPageInfo() throws IOException {
    willReturn(MOCK_URL).given(mediaProperties).getYoutubeUrl();
    willReturn(connection).given(connection).url(anyString());
    willReturn(connection).given(connection).data(anyString(), anyString());
    willReturn(connection).given(connection).headers(any());
    willReturn(connection).given(connection).userAgent(any());
    willReturn(Jsoup.parse(MOCK_BASIC_HTML)).given(connection).get();
    Document doc = service.getPageInfo(MOCK_KEYWORD);
    Assertions.assertNotNull(doc);
  }

  @Test
  void testGetPageInfoFail() throws IOException {
    willReturn(MOCK_URL).given(mediaProperties).getYoutubeUrl();
    willReturn(connection).given(connection).url(anyString());
    willReturn(connection).given(connection).data(anyString(), anyString());
    willReturn(connection).given(connection).headers(any());
    willReturn(connection).given(connection).userAgent(any());
    willThrow(new IOException()).given(connection).get();
    Document doc = service.getPageInfo(MOCK_KEYWORD);
    Assertions.assertNull(doc);
  }

  @Test
  void testGetLatestInfo() throws IOException {
    var result = service.getLatestInfo(readYtDocument());
    Assertions.assertNotNull(result);
    Assertions.assertEquals(10, result.size());
    Assertions.assertAll(
        () -> Assertions.assertEquals(
            "3 videos que no tienen explicación alguna",
            result.get(2).getVideoName()),
        () -> Assertions.assertEquals("hace 9 días", result.get(2).getPublishedTimeText()),
        () -> Assertions.assertEquals("hace 9 días", result.get(2).getPublishedTimeText()),
        () -> Assertions.assertEquals("1,777,032 vistas", result.get(2).getViewCountText()));
  }

  private Document readYtDocument() throws IOException {
    ClassPathResource resource = new ClassPathResource("youtube.html");
    byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
    String flvDoc = new String(bytes, StandardCharsets.UTF_8);
    return Jsoup.parse(flvDoc);
  }

}
