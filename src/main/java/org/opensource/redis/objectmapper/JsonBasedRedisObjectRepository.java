package org.opensource.redis.objectmapper;

import org.apache.commons.lang3.StringUtils;
import org.opensource.redis.objectmapper.entityinfo.EntityInfo;
import org.opensource.redis.objectmapper.entityinfo.EntityInfoFactory;
import org.opensource.redis.objectmapper.index.IndexHelper;
import org.opensource.redis.objectmapper.serializer.JsonBasedEntitySerializer;
import org.opensource.redis.objectmapper.serializer.KeySerializerHelper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @param <T>
 * @author xlpeng
 */
public class JsonBasedRedisObjectRepository<T> extends RedisObjectRepository<T> {

  private KeySerializerHelper keySerializerHelper = new KeySerializerHelper();
  private JsonBasedEntitySerializer<T> entitySerializer;
  private EntityInfo<T> entityInfo;

  JsonBasedRedisObjectRepository(String repositoryNamespace, Class<T> javaType, RedisTemplate<String, String> template) {
    super(repositoryNamespace, javaType, template);
    entitySerializer = JsonBasedEntitySerializer.getEntitySerializer(EntityInfoFactory.getEntityInfo(javaType));
    entityInfo = EntityInfoFactory.getEntityInfo(javaType);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected RedisTemplate<String, String> getRedisTemplate() {
    return super.getRedisTemplate();
  }

  @Override
  public void save(@Nonnull T t) {
    final Tris<String, String, Collection<String>> serializeResult = entitySerializer.serialize(t);

    final Collection<String> originalIndexes = new ArrayList<>();
    if (!EntityInfoFactory.getEntityInfo(t.getClass()).getIndexes().isEmpty()) {
      T original = getByKey(t);
      if (original != null) {
        Tris<String, String, Collection<String>> originalSerializeResult = entitySerializer.serialize(original);
        originalIndexes.addAll(originalSerializeResult.getThird());
      }
    }

    getRedisTemplate().execute(new SessionCallback<Object>() {
      @Override
      @SuppressWarnings("unchecked")
      public Object execute(RedisOperations operations) throws DataAccessException {
        try {
          operations.multi();
          //remove from old indexes if necessary
          if (!originalIndexes.isEmpty()) {
            for (String originalIndex : originalIndexes) {
              operations.opsForSet().remove(getFullKey(originalIndex), serializeResult.first);
            }
          }
          save(serializeResult, operations);
          operations.exec();
          return null;
        } catch (DataAccessException ex) {
          operations.discard();
          throw ex;
        }
      }
    });

  }

  protected void save(final Tris<String, String, Collection<String>> serializeResult, RedisOperations<String, String> operations) {
    operations.opsForValue().set(getFullKey(serializeResult.getFirst()), serializeResult.getSecond());
    //save index
    for (String indexKey : serializeResult.getThird()) {
      operations.opsForSet().add(getFullKey(indexKey), serializeResult.getFirst());
    }
  }

  @Override
  public void save(@Nonnull final List<T> objects) {
    for (T t : objects) {
      save(t);
    }
  }

  protected String getFullKey(String key) {
    return  getKeyPrefix() + key;
  }

  protected String getKeyPrefix(){
    return StringUtils.isNotBlank(getNamespace()) ? getNamespace() + ":" : "";
  }

  @Override
  public void deleteByKey(@Nonnull final T keyQuery) {
    T value = getByKey(keyQuery);
    if (value != null) {
      final Tris<String, String, Collection<String>> originalSerializeResult = entitySerializer.serialize(value);
      getRedisTemplate().execute(new SessionCallback<Object>() {
        @Override
        @SuppressWarnings("unchecked")
        public Object execute(RedisOperations operations) throws DataAccessException {
          operations.multi();

          operations.delete(getFullKey(originalSerializeResult.getFirst()));
          for (String indexKey : originalSerializeResult.getThird()) {
            operations.opsForSet().remove(getFullKey(indexKey), originalSerializeResult.getFirst());
          }

          operations.exec();
          return null;
        }
      });
    }
  }

  private void deleteByKeyString(final Collection<String> fullKeys) {
    final List<T> results = getByKeyString(fullKeys);
    getRedisTemplate().execute(new SessionCallback<Object>() {
      @Override
      @SuppressWarnings("unchecked")
      public Object execute(RedisOperations operations) throws DataAccessException {
        operations.multi();
        operations.delete(fullKeys);
        if (!entityInfo.getIndexes().isEmpty()) {
          Iterator<String> keyIterator = fullKeys.iterator();
          for (T t : results) {
            String entityKey = keyIterator.next();
            if (t != null) {
              for (String indexKey : IndexHelper.buildIndexKeys(t)) {
                operations.opsForSet().remove(indexKey, entityKey);
              }
            }
          }
        }
        operations.exec();
        return null;
      }
    });

  }

  public void deleteByKey(Collection<T> keyQuery) {
    List<String> fullKeys = new ArrayList<>(keyQuery.size());
    for (T t : keyQuery) {
      fullKeys.add(getFullKey(keySerializerHelper.serializeKey(t)));
    }
    deleteByKeyString(fullKeys);
  }

  @Override
  public void deleteByKeyPattern(@Nonnull T keyPatternQuery) {
    Set<String> keys = getRedisTemplate().keys(keySerializerHelper.serializeKey(keyPatternQuery));
    if (!keys.isEmpty()) {
      deleteByKeyString(keys);
    }
  }

  @Override
  public T getByKey(@Nonnull T keyQuery) {
    String key = getFullKey(keySerializerHelper.serializeKey(keyQuery));
    return getByKeyString(Arrays.asList(key)).get(0);
  }

  private List<T> getByKeyString(Collection<String> keys) {

    List<String> rawResults = getRedisTemplate().opsForValue().multiGet(keys);

    int keyPrefixLength = getKeyPrefix().length();
    List<T> results = new ArrayList<>(keys.size());
    Iterator<String> keysIterator = keys.iterator();
    for (String rawResult : rawResults) {
      String key = keysIterator.next();
      if (StringUtils.isNotBlank(rawResult)) {
        results.add(entitySerializer.deserialize(key.substring(keyPrefixLength, key.length()), rawResult));
      } else {
        results.add(null);
      }
    }

    return results;
  }

  public List<T> getByKey(List<T> keyQueries) {
    List<String> keys = new ArrayList<>(keyQueries.size());
    for (T t : keyQueries) {
      keys.add(getFullKey(keySerializerHelper.serializeKey(t)));
    }
    return getByKeyString(keys);
  }


  @Override
  public List<T> queryByKeyPattern(@Nonnull T keyPatternQuery) {
    String keyPattern = keySerializerHelper.serializeKey(keyPatternQuery);
    final Set<String> keys = getRedisTemplate().keys(keyPattern);
    if (keys.isEmpty()) {
      return Collections.emptyList();
    } else {
      return getByKeyString(keys);
    }
  }
}