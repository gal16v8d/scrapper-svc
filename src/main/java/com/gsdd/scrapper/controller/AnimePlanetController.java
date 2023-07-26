package com.gsdd.scrapper.controller;

import com.gsdd.scrapper.model.ap.AnimeInfo;
import com.gsdd.scrapper.services.AnimePlanetService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/ap/search")
public class AnimePlanetController {

  private final AnimePlanetService animePlanetService;

  @GetMapping("{page:[0-9]+}")
  public ResponseEntity<List<AnimeInfo>> extractAnimeInfo(@PathVariable int page) throws IOException {
    return ResponseEntity.ok(animePlanetService.extractAnimeInfo(page));
  }

  @GetMapping("all")
  public ResponseEntity<List<AnimeInfo>> extractAllAnimeInfo() {
    var doc = animePlanetService.getMainPageInfo();
    return ResponseEntity.ok(animePlanetService.extractAllAnimeInfo(doc));
  }
}
