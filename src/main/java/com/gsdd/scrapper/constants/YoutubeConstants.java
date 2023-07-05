package com.gsdd.scrapper.constants;

import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YoutubeConstants {

  public static final String EMPTY = "";
  public static final String EQUAL = "=";
  public static final String MOZILLA = "Mozilla";
  public static final String SEARCH_QUERY = "search_query";
  public static final String SEMICOLON = ";";
  public static final String SCRIPT_NONCE = "script[nonce]";
  public static final String YT_INITIAL_DATA = "var ytInitialData";
  public static final String YT_PAYLOAD_CONTENT = "content";
  public static final String YT_PAYLOAD_CONTENTS = "contents";
  public static final String YT_PAYLOAD_ITEMS = "items";
  public static final String YT_PAYLOAD_ITEM_SR = "itemSectionRenderer";
  public static final String YT_PAYLOAD_LENGTH_T = "lengthText";
  public static final String YT_PAYLOAD_PRIMARY_C = "primaryContents";
  public static final String YT_PAYLOAD_PUBLISHED_TIME_TEXT = "publishedTimeText";
  public static final String YT_PAYLOAD_RUNS = "runs";
  public static final String YT_PAYLOAD_SECTION_LR = "sectionListRenderer";
  public static final String YT_PAYLOAD_SHELF_R = "shelfRenderer";
  public static final String YT_PAYLOAD_SIMPLE_TEXT = "simpleText";
  public static final String YT_PAYLOAD_TEXT = "text";
  public static final String YT_PAYLOAD_TITLE = "title";
  public static final String YT_PAYLOAD_TWO_COL_SRR = "twoColumnSearchResultsRenderer";
  public static final String YT_PAYLOAD_VERTICAL_LR = "verticalListRenderer";
  public static final String YT_PAYLOAD_VIDEO_R = "videoRenderer";
  public static final String YT_PAYLOAD_VIEW_COUNT_T = "viewCountText";
  public static final Map<String, String> BASIC_HTTP_HEADERS = Map.of(
      "accept",
      "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
      "sec-fetch-site",
      "none",
      "sec-fetch-mode",
      "navigate",
      "accept-language",
      "en-GB,en-US;q=0.9,en;q=0.8");

}
