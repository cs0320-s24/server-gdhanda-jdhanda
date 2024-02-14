package edu.brown.cs.student.main.server.handlers.census;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

public class GenericCache<K, V> {
  private Cache<K, V> cache;

  public GenericCache(boolean sizeCap, int size, boolean timeCap, int minutes) {
    if (sizeCap && timeCap) {
      cache =
          CacheBuilder.newBuilder()
              .maximumSize(size)
              .expireAfterAccess(minutes, TimeUnit.MINUTES)
              .build();
    } else if (sizeCap) {
      cache = CacheBuilder.newBuilder().maximumSize(size).build();
    } else if (timeCap) {
      cache = CacheBuilder.newBuilder().expireAfterAccess(minutes, TimeUnit.MINUTES).build();
    } else {
      cache = CacheBuilder.newBuilder().build();
    }
  }

  public void put(K key, V value) {
    cache.put(key, value);
  }

  public V get(K key) {
    return cache.getIfPresent(key);
  }
}
