package com.gsdd.scrapper.services;

import com.gsdd.scrapper.model.yt.YoutubeInfo;
import java.util.List;
import org.jsoup.nodes.Document;

public interface YoutubeService {
  
  Document getPageInfo(String keyword);

  List<YoutubeInfo> getLatestInfo(Document doc);
}
