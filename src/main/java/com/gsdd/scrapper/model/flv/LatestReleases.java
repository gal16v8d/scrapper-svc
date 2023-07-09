package com.gsdd.scrapper.model.flv;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@ToString
public class LatestReleases {

  private String name;
  private String type;
}
