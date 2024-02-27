package com.gsdd.scrapper.services;

import com.gsdd.scrapper.model.ap.AnimeInfo;
import java.util.List;
import org.jsoup.nodes.Document;

public interface AnimePlanetService {

  Document getMainPageInfo();

  Document getPageInfo(int page);

  List<AnimeInfo> extractAnimeInfo(int page);

  List<AnimeInfo> extractAllAnimeInfo(Document doc);
}
