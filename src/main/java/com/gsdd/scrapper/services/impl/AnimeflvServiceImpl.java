package com.gsdd.scrapper.services.impl;

import com.gsdd.scrapper.constants.AnimeFlvConstants;
import com.gsdd.scrapper.constants.ScrapperConstants;
import com.gsdd.scrapper.model.flv.DetailedLatestReleases;
import com.gsdd.scrapper.model.flv.LatestAdds;
import com.gsdd.scrapper.model.flv.LatestReleases;
import com.gsdd.scrapper.properties.MediaProperties;
import com.gsdd.scrapper.services.AnimeFlvService;
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
public class AnimeflvServiceImpl implements AnimeFlvService {

  private final MediaProperties mediaProperties;
  private final Connection connection;

  @Cacheable(value = AnimeFlvConstants.CACHE_MAIN)
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

  @Cacheable(value = AnimeFlvConstants.CACHE_RELEASES)
  @Override
  public List<LatestReleases> getLatestReleases(Document doc) {
    var data = doc.getElementsByClass(AnimeFlvConstants.RELEASE_CLASS)
        .stream()
        .flatMap(element -> element.select(AnimeFlvConstants.LIST).stream())
        .map(this::toRelease)
        .toList();
    log.info("latest releases -> {}", data);
    return data;
  }

  @Cacheable(value = AnimeFlvConstants.CACHE_DETAILED_RELEASES)
  @Override
  public List<DetailedLatestReleases> getDetailedLatestReleases(Document doc) {
    var data = doc.getAllElements()
        .stream()
        .filter(element -> element.className().contains(AnimeFlvConstants.DETAILED_CLASS))
        .flatMap(element -> element.select(AnimeFlvConstants.LIST).stream())
        .map(this::toDetailedRelease)
        .toList();
    log.info("detailed latest releases -> {}", data);
    return data;
  }

  @Cacheable(value = AnimeFlvConstants.CACHE_ADD)
  @Override
  public List<LatestAdds> getLatestAdds(Document doc) {
    var data = doc.getAllElements()
        .stream()
        .filter(element -> element.className().contains(AnimeFlvConstants.ADD_CLASS))
        .flatMap(element -> element.select(AnimeFlvConstants.LIST_ADD).stream())
        .filter(element -> !AnimeFlvConstants.ADD_EXCLUDED.equals(element.className()))
        .map(this::toLatestAdd)
        .toList();
    log.info("latest adds -> {}", data);
    return data;
  }

  private LatestReleases toRelease(Element element) {
    return LatestReleases.builder()
        .name(element.ownText().trim())
        .type(element.select(AnimeFlvConstants.SPAN).text().trim())
        .build();
  }

  private DetailedLatestReleases toDetailedRelease(Element element) {
    return DetailedLatestReleases.builder()
        .name(element.select(AnimeFlvConstants.STRONG).text().trim())
        .episode(findTextFromClass(element, AnimeFlvConstants.CAPI))
        .build();
  }

  private LatestAdds toLatestAdd(Element element) {
    return LatestAdds.builder()
        .name(element.select(AnimeFlvConstants.ADD_TITLE).text().trim())
        .type(findTextFromClass(element, AnimeFlvConstants.ADD_TYPE))
        .rate(findTextFromClass(element, AnimeFlvConstants.ADD_RATE))
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
