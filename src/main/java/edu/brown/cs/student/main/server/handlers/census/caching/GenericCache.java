package edu.brown.cs.student.main.server.handlers.census.caching;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

public class GenericCache<K, V> {
  private Cache<K, V> cache;

  public GenericCache(boolean sizeCap, int size, boolean timeCap, int minutes) {
    if (sizeCap && timeCap) {
      this.cache =
          CacheBuilder.newBuilder()
              .maximumSize(size)
              .expireAfterAccess(minutes, TimeUnit.MINUTES)
              .build();
    } else if (sizeCap) {
      this.cache = CacheBuilder.newBuilder().maximumSize(size).build();
    } else if (timeCap) {
      this.cache = CacheBuilder.newBuilder().expireAfterAccess(minutes, TimeUnit.MINUTES).build();
    } else {
      this.cache = CacheBuilder.newBuilder().build();
    }
  }

  public void put(K key, V value) {
    cache.put(key, value);
  }

  public V get(K key) {
    return cache.getIfPresent(key);
  }
}
