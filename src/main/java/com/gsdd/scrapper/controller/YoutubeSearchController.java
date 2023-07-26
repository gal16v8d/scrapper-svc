package com.gsdd.scrapper.controller;

import com.gsdd.scrapper.model.yt.YoutubeInfo;
import com.gsdd.scrapper.services.YoutubeService;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/yt/search")
public class YoutubeSearchController {

  private final YoutubeService youtubeService;

  @GetMapping("{channelName}")
  public ResponseEntity<Collection<YoutubeInfo>>
      getLatestInfo(@PathVariable("channelName") String channelName) {
    var doc = youtubeService.getPageInfo(channelName);
    List<YoutubeInfo> infoList = youtubeService.getLatestInfo(doc);
    if (infoList == null || infoList.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(infoList);
  }

}
