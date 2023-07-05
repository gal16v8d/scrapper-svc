package com.gsdd.scrapper.services;

import com.gsdd.scrapper.model.YoutubeInfo;
import java.util.List;

public interface YoutubeService {

  List<YoutubeInfo> getLatestInfo(String keyword);
}
