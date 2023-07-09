package com.gsdd.scrapper.model.flv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@ToString
public class DetailedLatestReleases {

  private String name;
  private String episode;
}
