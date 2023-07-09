package com.gsdd.scrapper.services;

import com.gsdd.scrapper.model.flv.DetailedLatestReleases;
import com.gsdd.scrapper.model.flv.LatestAdds;
import com.gsdd.scrapper.model.flv.LatestReleases;
import java.util.List;
import org.jsoup.nodes.Document;

public interface AnimeflvService {

  Document getPageInfo();

  List<LatestReleases> getLatestReleases(Document doc);

  List<DetailedLatestReleases> getDetailedLatestReleases(Document doc);

  List<LatestAdds> getLatestAdds(Document doc);
}
