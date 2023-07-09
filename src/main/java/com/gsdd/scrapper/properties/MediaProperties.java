package com.gsdd.scrapper.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("media.source")
public class MediaProperties {

  private String animeFlvUrl;
  private String youtubeUrl;
}
