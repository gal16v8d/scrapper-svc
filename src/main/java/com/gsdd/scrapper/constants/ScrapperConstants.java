package com.gsdd.scrapper.constants;

import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ScrapperConstants {

  public static final String COMMA = ",";
  public static final String EMPTY = "";
  public static final String PARENTHESIS = "(";
  public static final String SPACE = " ";
  // Connection data
  public static final String REFERRER = "https://www.google.com/";
  public static final String MOZILLA =
      "Mozilla/5.0 (X11; Linux x86_64; rv:134.0) Gecko/20100101 Firefox/134.0";
  public static final Map<String,
      String> BASIC_HTTP_HEADERS_AP = Map.ofEntries(
          Map.entry("User-Agent", MOZILLA),
          Map.entry("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"),
          Map.entry("Accept-Language", "es,ja;q=0.7,en-US;q=0.3"),
          Map.entry("Accept-Encoding", "gzip, deflate, br, zstd"),
          Map.entry("DNT", "1"),
          Map.entry("Sec-GPC", "1"),
          Map.entry("Cookie", "darkmode=on"),
          Map.entry("Upgrade-Insecure-Requests", "1"),
          Map.entry("Sec-Fetch-Dest", "document"),
          Map.entry("Sec-Fetch-Mode", "navigate"),
          Map.entry("Sec-Fetch-Site", "none"),
          Map.entry("Sec-Fetch-User", "?1"),
          Map.entry("Priority", "u=0, i"));
  public static final Map<String, String> BASIC_HTTP_HEADERS = Map.of(
      "accept",
      "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
      "accept-language",
      "en-GB,en-US;q=0.9,en;q=0.8",
      "sec-fetch-site",
      "same-origin",
      "sec-fetch-mode",
      "navigate");
}
