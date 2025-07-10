package com.gsdd.scrapper.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsdd.scrapper.constants.ScrapperConstants;
import com.gsdd.scrapper.constants.YoutubeConstants;
import com.gsdd.scrapper.model.yt.YoutubeInfo;
import com.gsdd.scrapper.properties.MediaProperties;
import com.gsdd.scrapper.services.YoutubeService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class YoutubeServiceImpl implements YoutubeService {

  private final MediaProperties mediaProperties;
  private final Connection connection;
  private final ObjectMapper mapper;

  @Cacheable(value = YoutubeConstants.YT_CACHE_MAIN, key = "#keyword", unless = "#result == null")
  @Override
  public Document getPageInfo(String keyword) {
    log.debug("Querying latest youtube channel data for keyword: '{}'", keyword);
    String url = mediaProperties.getYoutubeUrl();
    Document doc = null;
    try {
      doc = connection.url(url)
          .data(YoutubeConstants.SEARCH_QUERY, keyword)
          .headers(ScrapperConstants.BASIC_HTTP_HEADERS)
          .userAgent(ScrapperConstants.MOZILLA)
          .referrer(ScrapperConstants.REFERRER)
          .get();
      log.info("doc: {}", doc);
    } catch (IOException e) {
      log.error("Can not connect to {}", url, e);
    }
    return doc;
  }

  @SuppressWarnings("unchecked")
  @Cacheable(value = YoutubeConstants.YT_CACHE_INFO, key = "#doc.hashCode()")
  @Override
  public List<YoutubeInfo> getLatestInfo(Document doc) {
    try {
      Elements scripts = doc.select(YoutubeConstants.SCRIPT_NONCE);

      String wholeData = scripts.dataNodes()
          .stream()
          .filter(Objects::nonNull)
          .filter(dn -> StringUtils.isNotBlank(dn.getWholeData()))
          .filter(dn -> dn.getWholeData().contains(YoutubeConstants.YT_INITIAL_DATA))
          .findAny()
          .map(DataNode::getWholeData)
          .orElse("");

      if (StringUtils.isNotBlank(wholeData)) {
        String tmpData = StringUtils.substringBeforeLast(wholeData, YoutubeConstants.SEMICOLON);
        tmpData = StringUtils.substringAfter(tmpData, YoutubeConstants.EQUAL);
        log.info("json data {}", tmpData);
        return getLatestVideos(mapper.readValue(tmpData, Map.class));
      }
    } catch (Exception e) {
      log.error("Error while accessing document data", e);
    }
    return Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> safeExtractMap(Map<String, Object> map, String key) {
    return (Map<String, Object>) map.getOrDefault(key, Collections.emptyMap());
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> safeExtractList(Map<String, Object> map, String key) {
    return (List<Map<String, Object>>) map.getOrDefault(key, Collections.emptyList());
  }

  private List<YoutubeInfo> getLatestVideos(Map<String, Object> dataMap) {
    List<YoutubeInfo> latestVideos = new ArrayList<>();
    Map<String, Object> contents = safeExtractMap(dataMap, YoutubeConstants.YT_PAYLOAD_CONTENTS);
    Map<String, Object> twoColumnSearchResultsRenderer =
        safeExtractMap(contents, YoutubeConstants.YT_PAYLOAD_TWO_COL_SRR);
    Map<String, Object> primaryContents =
        safeExtractMap(twoColumnSearchResultsRenderer, YoutubeConstants.YT_PAYLOAD_PRIMARY_C);
    Map<String, Object> sectionListRenderer =
        safeExtractMap(primaryContents, YoutubeConstants.YT_PAYLOAD_SECTION_LR);
    List<Map<String, Object>> internalContents =
        safeExtractList(sectionListRenderer, YoutubeConstants.YT_PAYLOAD_CONTENTS);
    if (!internalContents.isEmpty()) {
      Map<String, Object> channelContent = internalContents.getFirst();
      Map<String, Object> itemSectionRenderer =
          safeExtractMap(channelContent, YoutubeConstants.YT_PAYLOAD_ITEM_SR);
      List<Map<String, Object>> internalContents2 =
          safeExtractList(itemSectionRenderer, YoutubeConstants.YT_PAYLOAD_CONTENTS);
      if (!internalContents2.isEmpty()) {
        Map<String,
            Object> mainContent = internalContents2.stream()
                .filter(map -> map.containsKey(YoutubeConstants.YT_PAYLOAD_SHELF_R))
                .findFirst()
                .orElseGet(Collections::emptyMap);
        Map<String, Object> shelfRenderer =
            safeExtractMap(mainContent, YoutubeConstants.YT_PAYLOAD_SHELF_R);
        Map<String, Object> content =
            safeExtractMap(shelfRenderer, YoutubeConstants.YT_PAYLOAD_CONTENT);
        Map<String, Object> verticalListRenderer =
            safeExtractMap(content, YoutubeConstants.YT_PAYLOAD_VERTICAL_LR);
        List<Map<String, Object>> items =
            safeExtractList(verticalListRenderer, YoutubeConstants.YT_PAYLOAD_ITEMS);
        latestVideos = extractDataFromItem(items);
      }
    }
    return latestVideos;
  }

  private List<YoutubeInfo> extractDataFromItem(List<Map<String, Object>> items) {
    List<YoutubeInfo> infoList = new ArrayList<>();
    for (Map<String, Object> item : items) {
      Map<String, Object> videoRenderer = safeExtractMap(item, YoutubeConstants.YT_PAYLOAD_VIDEO_R);
      Map<String, Object> title = safeExtractMap(videoRenderer, YoutubeConstants.YT_PAYLOAD_TITLE);
      Map<String, Object> publishedTimeText =
          safeExtractMap(videoRenderer, YoutubeConstants.YT_PAYLOAD_PUBLISHED_TIME_TEXT);
      Map<String, Object> lengthText =
          safeExtractMap(videoRenderer, YoutubeConstants.YT_PAYLOAD_LENGTH_T);
      Map<String, Object> viewCountText =
          safeExtractMap(videoRenderer, YoutubeConstants.YT_PAYLOAD_VIEW_COUNT_T);
      List<Map<String, Object>> runs = safeExtractList(title, YoutubeConstants.YT_PAYLOAD_RUNS);
      YoutubeInfo info = new YoutubeInfo();
      info.setLengthText(
          (String) lengthText
              .getOrDefault(YoutubeConstants.YT_PAYLOAD_SIMPLE_TEXT, YoutubeConstants.EMPTY));
      info.setPublishedTimeText(
          (String) publishedTimeText
              .getOrDefault(YoutubeConstants.YT_PAYLOAD_SIMPLE_TEXT, YoutubeConstants.EMPTY));
      info.setViewCountText(
          (String) viewCountText
              .getOrDefault(YoutubeConstants.YT_PAYLOAD_SIMPLE_TEXT, YoutubeConstants.EMPTY));
      if (!runs.isEmpty()) {
        info.setVideoName(
            (String) runs.getFirst()
                .getOrDefault(YoutubeConstants.YT_PAYLOAD_TEXT, YoutubeConstants.EMPTY));
      }
      infoList.add(info);
    }
    return infoList;
  }
}
