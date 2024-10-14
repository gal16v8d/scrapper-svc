package com.gsdd.scrapper.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsdd.scrapper.model.flv.DetailedLatestReleases;
import com.gsdd.scrapper.model.flv.LatestAdds;
import com.gsdd.scrapper.model.flv.LatestReleases;
import com.gsdd.scrapper.services.AnimeFlvService;
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
@WebMvcTest(AnimeFlvController.class)
class AnimeFlvControllerTest {

  private static final String MOCK_EPISODE = "Episode 1";
  private static final String MOCK_NAME = "One Piece";
  private static final String MOCK_RATE = "5.0";
  private static final String MOCK_TYPE = "Anime";
  @Autowired
  private MockMvc mvc;
  @MockBean
  private AnimeFlvService animeflvService;

  @Test
  void testGetLatestAdded(@Mock Document doc) throws Exception {
    willReturn(doc).given(animeflvService).getPageInfo();
    willReturn(
        List.of(LatestAdds.builder().name(MOCK_NAME).type(MOCK_TYPE).rate(MOCK_RATE).build()))
        .given(animeflvService)
        .getLatestAdds(any());
    mvc.perform(get("/api/flv/search/added").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.[0].name").value(MOCK_NAME))
        .andExpect(jsonPath("$.[0].rate").value(MOCK_RATE));
  }

  @Test
  void testGetDetailedReleases(@Mock Document doc) throws Exception {
    willReturn(doc).given(animeflvService).getPageInfo();
    willReturn(
        List.of(DetailedLatestReleases.builder().name(MOCK_NAME).episode(MOCK_EPISODE).build()))
        .given(animeflvService)
        .getDetailedLatestReleases(any());
    mvc.perform(get("/api/flv/search/detailed").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.[0].name").value(MOCK_NAME))
        .andExpect(jsonPath("$.[0].episode").value(MOCK_EPISODE));
  }

  @Test
  void testGetReleases(@Mock Document doc) throws Exception {
    willReturn(doc).given(animeflvService).getPageInfo();
    willReturn(List.of(LatestReleases.builder().name(MOCK_NAME).type(MOCK_TYPE).build()))
        .given(animeflvService)
        .getLatestReleases(any());
    mvc.perform(get("/api/flv/search/releases").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.[0].name").value(MOCK_NAME))
        .andExpect(jsonPath("$.[0].type").value(MOCK_TYPE));
  }
}
