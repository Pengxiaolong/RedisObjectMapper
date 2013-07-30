package org.opensource.redis.objectmapper;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xlpeng
 */
@Service
public class JsonBasedRedisObjectRepositoryFactory implements RedisObjectRepositoryFactory {
  private Table<String, Class<?>, JsonBasedRedisObjectRepository<?>> cachedRedisObjectRepositories = HashBasedTable.create();
  private final ReentrantLock lock = new ReentrantLock();
  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Override
  @SuppressWarnings("unchecked")
  public <T> JsonBasedRedisObjectRepository<T> getRedisObjectRepository(String namespace, Class<T> javaType) {
    if (!cachedRedisObjectRepositories.contains(namespace, javaType)) {
      lock.lock();
      try {
        cachedRedisObjectRepositories.put(namespace, javaType, new JsonBasedRedisObjectRepository(namespace, javaType, redisTemplate));
      } finally {
        lock.unlock();
      }
    }
    return (JsonBasedRedisObjectRepository<T>) cachedRedisObjectRepositories.get(namespace, javaType);
  }
}