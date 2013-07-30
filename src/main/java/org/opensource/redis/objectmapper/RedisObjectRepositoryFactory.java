package org.opensource.redis.objectmapper;

/**
 * Created with IntelliJ IDEA.
 * User: xlpeng
 * Date: 13-7-30
 * Time: 上午1:33
 * To change this template use File | Settings | File Templates.
 */
public interface RedisObjectRepositoryFactory {
  <T> RedisObjectRepository<T> getRedisObjectRepository(String namespace, Class<T> javaType );
}
