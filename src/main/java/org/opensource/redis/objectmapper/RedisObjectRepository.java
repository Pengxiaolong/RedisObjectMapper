package org.opensource.redis.objectmapper;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author xlpeng
 * @param <T>
 */
public abstract class RedisObjectRepository<T> {
  protected String namespace;
  protected Class<T> javaType;
  protected RedisTemplate template;

  public RedisObjectRepository(String repositoryNamespace, Class<T> javaType, RedisTemplate template) {
    this.javaType = javaType;
    this.namespace = repositoryNamespace;
    this.template = template;
  }

  protected RedisTemplate getRedisTemplate(){
    return template;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public abstract void save(@Nonnull T object);
  public abstract void save(@Nonnull List<T> objects);
  public abstract void deleteByKey(@Nonnull T keyQuery);
  public abstract void deleteByKey(@Nonnull String keyString);
  public abstract void deleteByKeyPattern(@Nonnull T keyPatternQuery);
  public abstract void deleteByKeyPattern(@Nonnull String keyPatternString);
  /*
  public abstract void deleteByIndex(T indexQuery);
  public abstract void deleteByIndexPattern(T indexPattern);
  */
  public abstract T getByKey(@Nonnull T keyQuery);
  public abstract T getByKey(@Nonnull String keyString);
  public abstract List<T>  queryByKeyPattern(@Nonnull T keyPatternQuery);
  public abstract List<T>  queryByKeyPattern(@Nonnull String keyPatternString);
  /*
  public abstract List<T>  queryByIndex(T indexQuery);
  public abstract List<T> queryByIndexPattern(T indexPatternQuery);
  */
}