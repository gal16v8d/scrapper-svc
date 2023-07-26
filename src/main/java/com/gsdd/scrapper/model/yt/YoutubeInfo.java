package com.gsdd.scrapper.model.yt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YoutubeInfo {

  private String videoName;
  private String publishedTimeText;
  private String lengthText;
  private String viewCountText;
}
