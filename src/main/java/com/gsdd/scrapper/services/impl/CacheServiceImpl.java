package com.gsdd.scrapper.services.impl;

import com.gsdd.scrapper.constants.AnimePlanetConstants;
import com.gsdd.scrapper.services.CacheService;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CacheServiceImpl implements CacheService {

  private final CacheManager cacheManager;

  @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
  @Override
  public void clearSmallCache() {
    cacheManager.getCacheNames()
        .stream()
        .filter(cacheName -> !cacheName.startsWith(AnimePlanetConstants.AP))
        .map(cacheManager::getCache)
        .filter(Objects::nonNull)
        .forEach(Cache::invalidate);
  }

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
  @Override
  public void clearHeavyCache() {
    cacheManager.getCacheNames()
        .stream()
        .filter(cacheName -> cacheName.startsWith(AnimePlanetConstants.AP))
        .map(cacheManager::getCache)
        .filter(Objects::nonNull)
        .forEach(Cache::invalidate);
  }

}
