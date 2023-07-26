package com.gsdd.scrapper.model.ap;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@ToString
public class AnimeInfo {
  
  private String name;
  private String type;
  private String caps;
  private List<String> studios;
  private String year;
  private String season;
  private String rate;
  private String rank;
  private ZonedDateTime updatedOn;
  private List<String> tags;
  private List<String> contentWarnings; 
  
}
