package com.gsdd.scrapper.services.impl;

import com.gsdd.scrapper.constants.AnimePlanetConstants;
import com.gsdd.scrapper.constants.ScrapperConstants;
import com.gsdd.scrapper.model.ap.AnimeInfo;
import com.gsdd.scrapper.properties.MediaProperties;
import com.gsdd.scrapper.services.AnimePlanetService;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnimePlanetServiceImpl implements AnimePlanetService {

  private final MediaProperties mediaProperties;
  private final Connection connection;

  @Cacheable(value = AnimePlanetConstants.CACHE_MAIN_PAGE)
  @Override
  public Document getMainPageInfo() {
    log.debug("Querying latest planet info from main page");
    String url = mediaProperties.getAnimePlanetUrl();
    Document doc = null;
    try {
      doc = connection.url(url + "all")
          .headers(ScrapperConstants.BASIC_HTTP_HEADERS_AP)
          .userAgent(ScrapperConstants.MOZILLA)
          .referrer(ScrapperConstants.REFERRER)
          .ignoreHttpErrors(true)
          .followRedirects(true)
          .get();
      log.info("doc main: {}", doc);
    } catch (IOException e) {
      log.error("Can not connect to {}", url, e);
    }
    return doc;
  }

  @Cacheable(value = AnimePlanetConstants.CACHE_PAGE_NUMBER, key = "#page",
      unless = "#result == null")
  @Override
  public Document getPageInfo(int page) {
    log.debug("Querying latest planet info from page: '{}'", page);
    String url = mediaProperties.getAnimePlanetUrl();
    Document doc = null;
    try {
      doc = connection.url(url)
          .data(AnimePlanetConstants.QUERY_PAGE, String.valueOf(page))
          .data(AnimePlanetConstants.QUERY_ORDER, AnimePlanetConstants.QUERY_ORDER_VALUE)
          .data(AnimePlanetConstants.QUERY_SORT, AnimePlanetConstants.QUERY_SORT_VALUE)
          .headers(ScrapperConstants.BASIC_HTTP_HEADERS_AP)
          .userAgent(ScrapperConstants.MOZILLA)
          .referrer(ScrapperConstants.REFERRER)
          .get();
      log.info("doc: {}", doc);
    } catch (IOException e) {
      log.error("Can not connect to {} on page {}", url, page, e);
    }
    return doc;
  }

  @Cacheable(value = AnimePlanetConstants.CACHE_PAGE_INFO, key = "#page",
      unless = "#result.isEmpty()")
  @Override
  public List<AnimeInfo> extractAnimeInfo(int page) {
    var doc = getPageInfo(page);
    return extractPagedAnimeData(doc);
  }

  @Cacheable(value = AnimePlanetConstants.CACHE_PAGE_ALL_INFO, key = "#doc.hashCode()",
      unless = "#result.isEmpty()")
  @Override
  public List<AnimeInfo> extractAllAnimeInfo(Document doc) {
    int maxPages = extractMaxPage(doc);
    List<AnimeInfo> data = new ArrayList<>(extractPagedAnimeData(doc));

    if (maxPages > 1) {
      IntStream.rangeClosed(2, maxPages).boxed().forEach((Integer page) -> {
        await(AnimePlanetConstants.SLEEP_ON_PAGE_TIME);
        data.addAll(extractAnimeInfo(page));
      });
    }
    return data;
  }

  private int extractMaxPage(Document doc) {
    return doc.getElementsByClass(AnimePlanetConstants.NODE_PAGES)
        .stream()
        .flatMap(element -> element.select(AnimePlanetConstants.NODE_LIST).stream())
        .map(element -> element.ownText().trim())
        .filter(StringUtils::hasText)
        .map(Integer::valueOf)
        .sorted(Comparator.reverseOrder())
        .findFirst()
        .orElse(0);
  }

  private List<AnimeInfo> extractPagedAnimeData(Document doc) {
    AtomicInteger value = new AtomicInteger(0);
    return doc.getElementsByClass(AnimePlanetConstants.NODE_ANIME)
        .stream()
        .flatMap(element -> element.select(AnimePlanetConstants.NODE_LIST).stream())
        .map(element -> element.selectFirst(AnimePlanetConstants.A))
        .filter(Objects::nonNull)
        .map(element -> element.attr(AnimePlanetConstants.HREF))
        .map(href -> href.substring(href.lastIndexOf("/") + 1))
        .collect(Collectors.toSet())
        .stream()
        .map(name -> {
          // Wait on certain connections to avoid 429 status
          var currentValue = value.incrementAndGet();
          if (currentValue == AnimePlanetConstants.SLEEP_ON_PAGE_ELEMENT) {
            value.set(0);
            await(AnimePlanetConstants.SLEEP_ON_PAGE_TIME);
            return extractAnimeDetail(name);
          }
          return extractAnimeDetail(name);
        })
        .toList();
  }

  private void await(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private AnimeInfo extractAnimeDetail(String name) {
    Document doc = connectToAnimeDetailPage(name);
    return collectAnimeDetail(name, doc);
  }

  private Document connectToAnimeDetailPage(String name) {
    log.debug("Querying latest planet detailed info from anime: '{}'", name);
    String url = mediaProperties.getAnimePlanetUrl();
    Document doc = null;
    try {
      log.debug("Fetching url -> {}", url + name);
      doc = connection.url(url + name)
          .headers(ScrapperConstants.BASIC_HTTP_HEADERS_AP)
          .userAgent(ScrapperConstants.MOZILLA)
          .referrer(ScrapperConstants.REFERRER)
          .get();
      log.info("doc: {}", doc);
    } catch (IOException e) {
      log.error("Can not connect to {} on anime page {}", url, name, e);
    }
    return doc;

  }

  private AnimeInfo collectAnimeDetail(String name, Document doc) {
    AnimeInfo.AnimeInfoBuilder builder = AnimeInfo.builder();
    builder.name(name);
    builder.updatedOn(ZonedDateTime.now(ZoneOffset.UTC));
    doc.getElementsByClass(AnimePlanetConstants.NODE_DETAIL)
        .stream()
        .findAny()
        .ifPresent(
            e -> iterateDocInfo(
                e.getElementsByClass(AnimePlanetConstants.NODE_INFO).stream().toList(),
                builder));
    List<String> tags = doc.getElementsByClass(AnimePlanetConstants.TAGS)
        .stream()
        .flatMap(element -> element.select(AnimePlanetConstants.NODE_LIST).stream())
        .map(e -> e.ownText().trim())
        .toList();
    if (!tags.isEmpty()) {
      builder.tags(tags);
    }
    var warning = doc.getElementsByClass(AnimePlanetConstants.TAGS_WARNING)
        .stream()
        .flatMap(element -> element.select(AnimePlanetConstants.NODE_LIST).stream())
        .map(e -> e.ownText().trim().replace(ScrapperConstants.COMMA, ScrapperConstants.EMPTY))
        .toList();
    if (!warning.isEmpty()) {
      builder.contentWarnings(warning);
    }
    return builder.build();
  }

  private void iterateDocInfo(List<Element> elements, AnimeInfo.AnimeInfoBuilder builder) {
    AtomicInteger pos = new AtomicInteger(0);
    elements.forEach((Element e) -> {
      assignData(e, pos.get(), builder);
      pos.incrementAndGet();
    });
  }

  private void assignData(Element e, int index, AnimeInfo.AnimeInfoBuilder builder) {
    switch (index) {
      case 0 -> assignTypeAndCapData(e, builder);
      case 1 -> assignStudioData(e, builder);
      case 2 -> assignSeasonData(e, builder);
      case 3 -> assignRateData(e, builder);
      default -> assignRank(e, builder);
    }
  }

  private void assignTypeAndCapData(Element e, AnimeInfo.AnimeInfoBuilder builder) {
    e.getElementsByClass(AnimePlanetConstants.TYPE)
        .stream()
        .findAny()
        .ifPresent((Element typeElement) -> {
          String value = typeElement.ownText().trim();
          String[] data = value.split(ScrapperConstants.SPACE);
          builder.type(data[0]);
          builder.caps(data[1].replace(ScrapperConstants.PARENTHESIS, ScrapperConstants.EMPTY));
        });
  }

  private void assignStudioData(Element e, AnimeInfo.AnimeInfoBuilder builder) {
    List<String> studios = e.select(AnimePlanetConstants.A_HREF)
        .stream()
        .map(studioElement -> studioElement.ownText().trim())
        .toList();
    if (!studios.isEmpty()) {
      builder.studios(studios);
    }
  }

  private void assignSeasonData(Element e, AnimeInfo.AnimeInfoBuilder builder) {
    e.select(AnimePlanetConstants.SPAN)
        .stream()
        .findAny()
        .ifPresent(yearElement -> builder.year(yearElement.ownText().trim()));
    e.select(AnimePlanetConstants.A_HREF)
        .stream()
        .findAny()
        .ifPresent(seasonElement -> builder.season(seasonElement.ownText().trim()));
  }

  private void assignRateData(Element e, AnimeInfo.AnimeInfoBuilder builder) {
    builder.rate(
        Optional.ofNullable(e.selectFirst(AnimePlanetConstants.AVG_RATING))
            .map(ratingElement -> ratingElement.attr(AnimePlanetConstants.TITLE))
            .map(String::trim)
            .orElse(null));
  }

  private void assignRank(Element e, AnimeInfo.AnimeInfoBuilder builder) {
    builder.rank(e.ownText().trim());
  }

}
