package com.gsdd.scrapper.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gsdd.scrapper.model.ap.AnimeInfo;
import com.gsdd.scrapper.services.AnimePlanetService;
import java.util.List;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AnimePlanetController.class)
class AnimePlanetControllerTest {

  private static final String MOCK_NAME = "One Piece";
  @Autowired
  private MockMvc mvc;
  @MockitoBean
  private AnimePlanetService animePlanetService;

  @Test
  void testExtractAnimeInfo() throws Exception {
    willReturn(List.of(AnimeInfo.builder().name(MOCK_NAME).build())).given(animePlanetService)
        .extractAnimeInfo(anyInt());
    mvc.perform(get("/api/ap/search/1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.[0].name").value(MOCK_NAME));
  }

  @Test
  void testExtractAllAnimeInfo(@Mock Document doc) throws Exception {
    willReturn(doc).given(animePlanetService).getMainPageInfo();
    willReturn(List.of(AnimeInfo.builder().name(MOCK_NAME).build())).given(animePlanetService)
        .extractAllAnimeInfo(any());
    mvc.perform(get("/api/ap/search/all").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.[0].name").value(MOCK_NAME));
  }
}
