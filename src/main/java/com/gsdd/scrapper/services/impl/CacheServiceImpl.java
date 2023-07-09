package com.gsdd.scrapper.services.impl;

import com.gsdd.scrapper.services.CacheService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CacheServiceImpl implements CacheService {

  private final CacheManager cacheManager;

  @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
  @Override
  public void clearCache() {
    cacheManager.getCacheNames()
        .stream()
        .forEach(cacheName -> cacheManager.getCache(cacheName).invalidate());
  }

}
