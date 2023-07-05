package com.gsdd.scrapper.model;

import lombok.Data;

@Data
public class YoutubeInfo {

  private String videoName;
  private String publishedTimeText;
  private String lengthText;
  private String viewCountText;
}
