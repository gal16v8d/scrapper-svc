package com.gsdd.scrapper.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsdd.scrapper.model.yt.YoutubeInfo;
import com.gsdd.scrapper.services.YoutubeService;
import java.util.List;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(YoutubeSearchController.class)
class YoutubeSearchControllerTest {

  private static final String MOCK_NAME = "Stronger Than You - Trio (Sans/Chara/Frisk)";
  private static final String MOCK_VIEW = "32,946,958";
  @Autowired
  private MockMvc mvc;
  @MockBean
  private YoutubeService youtubeService;

  @Test
  void testGetLatestInfo(@Mock Document doc) throws Exception {
    willReturn(doc).given(youtubeService).getPageInfo(any());
    willReturn(
        List.of(
            YoutubeInfo.builder()
                .videoName(MOCK_NAME)
                .publishedTimeText("hace 6 años")
                .lengthText("")
                .viewCountText(MOCK_VIEW)
                .build()))
        .given(youtubeService)
        .getLatestInfo(any());
    mvc.perform(get("/api/yt/search/Rayani").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.[0].videoName").value(MOCK_NAME))
        .andExpect(jsonPath("$.[0].viewCountText").value(MOCK_VIEW));
  }

  @Test
  void testGetLatestInfoNullNotFound(@Mock Document doc) throws Exception {
    willReturn(doc).given(youtubeService).getPageInfo(any());
    willReturn(null).given(youtubeService).getLatestInfo(any());
    mvc.perform(get("/api/yt/search/Rayani").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());
  }
  
  @Test
  void testGetLatestInfoEmptyNotFound(@Mock Document doc) throws Exception {
    willReturn(doc).given(youtubeService).getPageInfo(any());
    willReturn(List.of()).given(youtubeService).getLatestInfo(any());
    mvc.perform(get("/api/yt/search/Rayani").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound());
  }
}
