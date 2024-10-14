package com.gsdd.scrapper.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.spy;

import com.gsdd.scrapper.properties.MediaProperties;
import com.gsdd.scrapper.services.AnimeFlvService;
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
class AnimeFlvServiceImplTest {

  private static final String MOCK_URL = "http://dummy-page.net";
  private static final String MOCK_BASIC_HTML = "<html><body><h1>Mocked HTML</h1></body></html>";
  @Mock
  private MediaProperties mediaProperties;
  @Mock
  private Connection connection;
  private AnimeFlvService service;

  @BeforeEach
  void setUp() {
    service = spy(new AnimeflvServiceImpl(mediaProperties, connection));
  }

  @Test
  void testGetPageInfo() throws IOException {
    willReturn(MOCK_URL).given(mediaProperties).getAnimeFlvUrl();
    willReturn(connection).given(connection).url(anyString());
    willReturn(connection).given(connection).headers(any());
    willReturn(connection).given(connection).userAgent(any());
    willReturn(Jsoup.parse(MOCK_BASIC_HTML)).given(connection).get();
    Document doc = service.getPageInfo();
    Assertions.assertNotNull(doc);
  }

  @Test
  void testGetPageInfoFail() throws IOException {
    willReturn(MOCK_URL).given(mediaProperties).getAnimeFlvUrl();
    willReturn(connection).given(connection).url(anyString());
    willReturn(connection).given(connection).headers(any());
    willReturn(connection).given(connection).userAgent(any());
    willThrow(new IOException()).given(connection).get();
    Document doc = service.getPageInfo();
    Assertions.assertNull(doc);
  }

  @Test
  void testGetLatestReleases() throws IOException {
    var result = service.getLatestReleases(readFlvDocument());
    Assertions.assertNotNull(result);
    Assertions.assertEquals(21, result.size());
    Assertions.assertAll(
        () -> Assertions.assertEquals("Anime", result.getFirst().getType()),
        () -> Assertions.assertEquals("One Piece", result.getFirst().getName()));
  }

  @Test
  void testGetDetailedLatestReleases() throws IOException {
    var result = service.getDetailedLatestReleases(readFlvDocument());
    Assertions.assertNotNull(result);
    Assertions.assertEquals(20, result.size());
    Assertions.assertAll(
        () -> Assertions.assertEquals("Suki na Ko ga Megane wo Wasureta", result.get(0).getName()),
        () -> Assertions.assertEquals("Episodio 1", result.getFirst().getEpisode()));
  }

  @Test
  void testGetLatestAdds() throws IOException {
    var result = service.getLatestAdds(readFlvDocument());
    Assertions.assertNotNull(result);
    Assertions.assertEquals(24, result.size());
    Assertions.assertAll(
        () -> Assertions.assertEquals("Suki na Ko ga Megane wo Wasureta", result.get(0).getName()),
        () -> Assertions.assertEquals("Anime", result.getFirst().getType()),
        () -> Assertions.assertEquals("4.4", result.getFirst().getRate()));
  }

  private Document readFlvDocument() throws IOException {
    ClassPathResource resource = new ClassPathResource("animeflv.html");
    byte[] bytes = StreamUtils.copyToByteArray(resource.getInputStream());
    String flvDoc = new String(bytes, StandardCharsets.UTF_8);
    return Jsoup.parse(flvDoc);
  }

}
