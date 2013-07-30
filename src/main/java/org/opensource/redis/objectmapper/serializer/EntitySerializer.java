package org.opensource.redis.objectmapper.serializer;

import org.opensource.redis.objectmapper.entityinfo.EntityInfo;
import org.opensource.redis.objectmapper.Tris;

import java.util.Collection;

/**
  * @param <T> type of  data object
  * @param <R> type of serialize result
  */
public interface EntitySerializer<T, R> {
  Tris<String, R, Collection<String>> serialize(T t);
  Object deserialize(String key, R serializeResult);
}
