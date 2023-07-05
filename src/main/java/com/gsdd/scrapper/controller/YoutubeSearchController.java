package com.gsdd.scrapper.controller;

import com.gsdd.scrapper.model.YoutubeInfo;
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
@RequestMapping("api/ytsearch")
public class YoutubeSearchController {

  private final YoutubeService youtubeService;

  @GetMapping("{channelName}")
  public ResponseEntity<Collection<YoutubeInfo>>
      getLatestInfo(@PathVariable("channelName") String channelName) {
    List<YoutubeInfo> infoList = youtubeService.getLatestInfo(channelName);
    if (infoList == null || infoList.isEmpty()) {
      ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(infoList);
  }

}
