package com.gsdd.scrapper.controller;

import com.gsdd.scrapper.model.flv.DetailedLatestReleases;
import com.gsdd.scrapper.model.flv.LatestAdds;
import com.gsdd.scrapper.model.flv.LatestReleases;
import com.gsdd.scrapper.services.AnimeflvService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/flv/search")
public class AnimeFlvController {

  private final AnimeflvService animeflvService;

  @GetMapping("added")
  public ResponseEntity<List<LatestAdds>> getLatestAdded() {
    var doc = animeflvService.getPageInfo();
    return ResponseEntity.ok(animeflvService.getLatestAdds(doc));
  }

  @GetMapping("detailed")
  public ResponseEntity<List<DetailedLatestReleases>> getDetailedReleases() {
    var doc = animeflvService.getPageInfo();
    return ResponseEntity.ok(animeflvService.getDetailedLatestReleases(doc));
  }

  @GetMapping("releases")
  public ResponseEntity<List<LatestReleases>> getReleases() {
    var doc = animeflvService.getPageInfo();
    return ResponseEntity.ok(animeflvService.getLatestReleases(doc));
  }

}
