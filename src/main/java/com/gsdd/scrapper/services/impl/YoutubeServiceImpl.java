package com.gsdd.scrapper.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsdd.scrapper.constants.ScrapperConstants;
import com.gsdd.scrapper.constants.YoutubeConstants;
import com.gsdd.scrapper.model.YoutubeInfo;
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
  private List<YoutubeInfo> getLatestVideos(Map<String, Object> dataMap) {
    List<YoutubeInfo> latestVideos = new ArrayList<>();
    Map<String, Object> contents = (Map<String, Object>) dataMap
        .getOrDefault(YoutubeConstants.YT_PAYLOAD_CONTENTS, Collections.emptyMap());
    Map<String, Object> twoColumnSearchResultsRenderer = (Map<String, Object>) contents
        .getOrDefault(YoutubeConstants.YT_PAYLOAD_TWO_COL_SRR, Collections.emptyMap());
    Map<String, Object> primaryContents = (Map<String, Object>) twoColumnSearchResultsRenderer
        .getOrDefault(YoutubeConstants.YT_PAYLOAD_PRIMARY_C, Collections.emptyMap());
    Map<String, Object> sectionListRenderer = (Map<String, Object>) primaryContents
        .getOrDefault(YoutubeConstants.YT_PAYLOAD_SECTION_LR, Collections.emptyMap());
    List<Map<String, Object>> internalContents = (List<Map<String, Object>>) sectionListRenderer
        .getOrDefault(YoutubeConstants.YT_PAYLOAD_CONTENTS, Collections.emptyList());
    if (!internalContents.isEmpty()) {
      Map<String, Object> channelContent = internalContents.get(0);
      Map<String, Object> itemSectionRenderer = (Map<String, Object>) channelContent
          .getOrDefault(YoutubeConstants.YT_PAYLOAD_ITEM_SR, Collections.emptyMap());
      List<Map<String, Object>> internalContents2 = (List<Map<String, Object>>) itemSectionRenderer
          .getOrDefault(YoutubeConstants.YT_PAYLOAD_CONTENTS, Collections.emptyList());
      if (!internalContents2.isEmpty()) {
        Map<String,
            Object> mainContent = internalContents2.stream()
                .filter(map -> map.containsKey(YoutubeConstants.YT_PAYLOAD_SHELF_R))
                .findFirst()
                .orElseGet(Collections::emptyMap);
        Map<String, Object> shelfRenderer = (Map<String, Object>) mainContent
            .getOrDefault(YoutubeConstants.YT_PAYLOAD_SHELF_R, Collections.emptyMap());
        Map<String, Object> content = (Map<String, Object>) shelfRenderer
            .getOrDefault(YoutubeConstants.YT_PAYLOAD_CONTENT, Collections.emptyMap());
        Map<String, Object> verticalListRenderer = (Map<String, Object>) content
            .getOrDefault(YoutubeConstants.YT_PAYLOAD_VERTICAL_LR, Collections.emptyMap());
        List<Map<String, Object>> items = (List<Map<String, Object>>) verticalListRenderer
            .getOrDefault(YoutubeConstants.YT_PAYLOAD_ITEMS, Collections.emptyList());
        latestVideos = extractDataFromItem(items);
      }
    }
    return latestVideos;
  }

  @SuppressWarnings("unchecked")
  private List<YoutubeInfo> extractDataFromItem(List<Map<String, Object>> items) {
    List<YoutubeInfo> infoList = new ArrayList<>();
    for (Map<String, Object> item : items) {
      Map<String, Object> videoRenderer = (Map<String, Object>) item
          .getOrDefault(YoutubeConstants.YT_PAYLOAD_VIDEO_R, Collections.emptyMap());
      Map<String, Object> title = (Map<String, Object>) videoRenderer
          .getOrDefault(YoutubeConstants.YT_PAYLOAD_TITLE, Collections.emptyMap());
      Map<String, Object> publishedTimeText = (Map<String, Object>) videoRenderer
          .getOrDefault(YoutubeConstants.YT_PAYLOAD_PUBLISHED_TIME_TEXT, Collections.emptyMap());
      Map<String, Object> lengthText = (Map<String, Object>) videoRenderer
          .getOrDefault(YoutubeConstants.YT_PAYLOAD_LENGTH_T, Collections.emptyMap());
      Map<String, Object> viewCountText = (Map<String, Object>) videoRenderer
          .getOrDefault(YoutubeConstants.YT_PAYLOAD_VIEW_COUNT_T, Collections.emptyMap());
      List<Map<String, Object>> runs = (List<Map<String, Object>>) title
          .getOrDefault(YoutubeConstants.YT_PAYLOAD_RUNS, Collections.emptyList());
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
            (String) runs.get(0)
                .getOrDefault(YoutubeConstants.YT_PAYLOAD_TEXT, YoutubeConstants.EMPTY));
      }
      infoList.add(info);
    }
    return infoList;
  }
}
