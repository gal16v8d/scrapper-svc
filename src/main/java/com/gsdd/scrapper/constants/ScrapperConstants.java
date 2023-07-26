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
  public static final String MOZILLA = "Mozilla";
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
