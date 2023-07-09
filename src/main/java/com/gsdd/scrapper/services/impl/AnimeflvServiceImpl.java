package com.gsdd.scrapper.services.impl;

import com.gsdd.scrapper.constants.AnimeflvConstants;
import com.gsdd.scrapper.constants.ScrapperConstants;
import com.gsdd.scrapper.model.flv.DetailedLatestReleases;
import com.gsdd.scrapper.model.flv.LatestAdds;
import com.gsdd.scrapper.model.flv.LatestReleases;
import com.gsdd.scrapper.properties.MediaProperties;
import com.gsdd.scrapper.services.AnimeflvService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnimeflvServiceImpl implements AnimeflvService {

  private final MediaProperties mediaProperties;
  private final Connection connection;

  @Cacheable(value = AnimeflvConstants.CACHE_MAIN)
  @Override
  public Document getPageInfo() {
    String url = mediaProperties.getAnimeFlvUrl();
    Document doc = null;
    try {
      doc = connection.url(url)
          .headers(ScrapperConstants.BASIC_HTTP_HEADERS)
          .userAgent(ScrapperConstants.MOZILLA)
          .get();
      log.info("doc: {}", doc);
    } catch (IOException e) {
      log.error("Can not connect to {}", url, e);
    }
    return doc;
  }

  @Cacheable(value = AnimeflvConstants.CACHE_RELEASES)
  @Override
  public List<LatestReleases> getLatestReleases(Document doc) {
    var data = doc.getElementsByClass(AnimeflvConstants.RELEASE_CLASS)
        .stream()
        .flatMap(element -> element.select(AnimeflvConstants.LIST).stream())
        .map(this::toRelease)
        .toList();
    log.info("latest releases -> {}", data);
    return data;
  }

  @Cacheable(value = AnimeflvConstants.CACHE_DETAILED_RELEASES)
  @Override
  public List<DetailedLatestReleases> getDetailedLatestReleases(Document doc) {
    var data = doc.getAllElements()
        .stream()
        .filter(element -> element.className().contains(AnimeflvConstants.DETAILED_CLASS))
        .flatMap(element -> element.select(AnimeflvConstants.LIST).stream())
        .map(this::toDetailedRelease)
        .toList();
    log.info("detailed latest releases -> {}", data);
    return data;
  }

  @Cacheable(value = AnimeflvConstants.CACHE_ADD)
  @Override
  public List<LatestAdds> getLatestAdds(Document doc) {
    var data = doc.getAllElements()
        .stream()
        .filter(element -> element.className().contains(AnimeflvConstants.ADD_CLASS))
        .flatMap(element -> element.select(AnimeflvConstants.LIST_ADD).stream())
        .filter(element -> !AnimeflvConstants.ADD_EXCLUDED.equals(element.className()))
        .map(this::toLatestAdd)
        .toList();
    log.info("latest adds -> {}", data);
    return data;
  }
  
  private LatestReleases toRelease(Element element) {
    return LatestReleases.builder()
        .name(element.ownText().trim())
        .type(element.select(AnimeflvConstants.SPAN).text().trim())
        .build();
  }

  private DetailedLatestReleases toDetailedRelease(Element element) {
    return DetailedLatestReleases.builder()
        .name(element.select(AnimeflvConstants.STRONG).text().trim())
        .episode(findTextFromClass(element, AnimeflvConstants.CAPI))
        .build();
  }

  private LatestAdds toLatestAdd(Element element) {
    return LatestAdds.builder()
        .name(element.select(AnimeflvConstants.ADD_TITLE).text().trim())
        .type(findTextFromClass(element, AnimeflvConstants.ADD_TYPE))
        .rate(findTextFromClass(element, AnimeflvConstants.ADD_RATE))
        .build();
  }

  private String findTextFromClass(Element element, String clazz) {
    return element.getElementsByClass(clazz)
        .stream()
        .findAny()
        .map(Element::text)
        .orElse("")
        .trim();
  }

}
