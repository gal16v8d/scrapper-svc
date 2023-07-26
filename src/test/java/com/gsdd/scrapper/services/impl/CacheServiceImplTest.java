package com.gsdd.scrapper.services.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;

import com.gsdd.scrapper.constants.AnimePlanetConstants;
import com.gsdd.scrapper.constants.AnimeflvConstants;
import com.gsdd.scrapper.services.CacheService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class CacheServiceImplTest {

  @Mock
  private CacheManager cacheManager;
  private CacheService service;

  @BeforeEach
  void setUp() {
    service = spy(new CacheServiceImpl(cacheManager));
  }

  @Test
  void testClearSmallCache(@Mock Cache cache) {
    willReturn(List.of(AnimePlanetConstants.CACHE_MAIN_PAGE, AnimeflvConstants.CACHE_ADD))
        .given(cacheManager)
        .getCacheNames();
    willReturn(cache).given(cacheManager).getCache(anyString());
    service.clearSmallCache();
    then(cacheManager).should().getCache(AnimeflvConstants.CACHE_ADD);
    then(cacheManager).should(never()).getCache(AnimePlanetConstants.CACHE_MAIN_PAGE);
  }

  @Test
  void testClearHeavyCache(@Mock Cache cache) {
    willReturn(List.of(AnimePlanetConstants.CACHE_MAIN_PAGE, AnimeflvConstants.CACHE_ADD))
        .given(cacheManager)
        .getCacheNames();
    willReturn(cache).given(cacheManager).getCache(anyString());
    service.clearHeavyCache();
    then(cacheManager).should(never()).getCache(AnimeflvConstants.CACHE_ADD);
    then(cacheManager).should().getCache(AnimePlanetConstants.CACHE_MAIN_PAGE);
  }
}
