package org.opensource.redis.objectmapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
    save(Arrays.asList(t));
  }

  @Override
  public void save(@Nonnull final List<T> objects) {
    final Iterator<T> originalEntities = getByKey((T[]) objects.toArray()).iterator();
    getRedisTemplate().execute(new SessionCallback<Object>() {
      @Override
      @SuppressWarnings("unchecked")
      public Object execute(final RedisOperations operations) throws DataAccessException {
        try {
          operations.multi();
          for (T t : objects) {
            T originalEntity = originalEntities.next();
            Collection<String> originalIndexes = originalEntity == null ? Collections.<String>emptySet() : IndexHelper.buildIndexKeys(originalEntity);
            Tris<String, String, Collection<String>> serializeResult = entitySerializer.serialize(t);
            operations.opsForValue().set(getFullKey(serializeResult.getFirst()), serializeResult.getSecond());

            for (String indexKey : serializeResult.getThird()) {
              if (!originalIndexes.contains(indexKey)) {
                operations.opsForSet().add(getFullKey(indexKey), serializeResult.getFirst());
              }
            }

            for (String originalIndex : originalIndexes) {
              if (!serializeResult.getThird().contains(originalIndex))
                operations.opsForSet().remove(getFullKey(originalIndex), serializeResult.first);
            }

          }

          operations.exec();
        } catch (DataAccessException ex) {
          operations.discard();
          throw ex;
        }
        return null;
      }
    });

  }

  protected String getFullKey(String key) {
    return getKeyPrefix() + key;
  }

  private volatile String keyPrefix;

  protected String getKeyPrefix() {
    if (keyPrefix == null) {
      synchronized (this) {
        if (keyPrefix == null) {
          keyPrefix = StringUtils.isNotBlank(getNamespace()) ? getNamespace() + ":" + entityInfo.getName() + ":"
                  : entityInfo.getName() + ":";
        }
      }
    }
    return keyPrefix;
  }

  @Override
  public void deleteByKey(@Nonnull final T keyQuery) {
    deleteByKeyString(Lists.newArrayList(getFullKey(keySerializerHelper.serializeKey(keyQuery))));
  }

  @Override
  public void deleteByKey(T[] keyQueries) {
    List<String> fullKeys = new ArrayList<>(keyQueries.length);
    for (T t : keyQueries) {
      fullKeys.add(getFullKey(keySerializerHelper.serializeKey(t)));
    }
    deleteByKeyString(fullKeys);
  }

  @Override
  public void deleteByKey(@Nonnull String keyString) {
    String fullKey = getFullKey(keyString);
    deleteByKeyString(Sets.newHashSet(fullKey));
  }

  @Override
  public void deleteByKey(@Nonnull String[] keyStrings) {
    List<String> fullKeys = new ArrayList<>(keyStrings.length);
    for (String keyString : keyStrings) {
      fullKeys.add(getFullKey(keyString));
    }
    deleteByKeyString(fullKeys);
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
          Iterator<String> fullKeyIterator = fullKeys.iterator();
          for (T t : results) {
            String fullEntityKey = fullKeyIterator.next();
            if (t != null) {
              for (String indexKey : IndexHelper.buildIndexKeys(t)) {
                operations.opsForSet().remove(indexKey, fullEntityKey.substring(getKeyPrefix().length()));
              }
            }
          }
        }
        operations.exec();
        return null;
      }
    });

  }

  @Override
  public void deleteByKeyPattern(@Nonnull T keyPatternQuery) {
    Set<String> keys = getRedisTemplate().keys(getFullKey(keySerializerHelper.serializeKeyPattern(keyPatternQuery)));
    if (!keys.isEmpty()) {
      deleteByKeyString(keys);
    }
  }

  @Override
  public void deleteByKeyPattern(@Nonnull String keyPatternString) {
    String fullKeyPatternString = getFullKey(keyPatternString);
    Set<String> keys = getRedisTemplate().keys(fullKeyPatternString);
    if (!keys.isEmpty()) {
      deleteByKeyString(keys);
    }
  }

  @Override
  public T getByKey(@Nonnull T keyQuery) {
    List<T> ret = getByKeyString(Arrays.asList(getFullKey(keySerializerHelper.serializeKey(keyQuery))));
    return ret.isEmpty() ? null : ret.get(0);
  }

  @Override
  public List<T> getByKey(@Nonnull T[] keyQueries) {
    List<String> fullKeys = new ArrayList<>(keyQueries.length);
    for (T keyQuery : keyQueries) {
      fullKeys.add(getFullKey(keySerializerHelper.serializeKey(keyQuery)));
    }
    return getByKeyString(fullKeys);
  }

  @Override
  public T getByKey(@Nonnull String keyString) {
    String fullKeyString = getFullKey(keyString);
    List<T> ret = getByKeyString(Arrays.asList(fullKeyString));
    return ret.isEmpty() ? null : ret.get(0);
  }

  @Override
  public List<T> getByKey(@Nonnull String[] keyStrings) {
    List<String> fullKeys = new ArrayList<>(keyStrings.length);
    for (String keyString : keyStrings) {
      fullKeys.add(getFullKey(keyString));
    }
    return getByKeyString(fullKeys);  //To change body of implemented methods use File | Settings | File Templates.
  }

  private List<T> getByKeyString(@Nonnull Collection<String> fullRedisKeys) {

    if (fullRedisKeys.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> rawResults = getRedisTemplate().opsForValue().multiGet(fullRedisKeys);

    int keyPrefixLength = getKeyPrefix().length();
    List<T> results = new ArrayList<>(fullRedisKeys.size());
    Iterator<String> keysIterator = fullRedisKeys.iterator();
    for (String rawResult : rawResults) {
      String key = keysIterator.next();
      if (StringUtils.isNotBlank(rawResult)) {
        results.add(entitySerializer.deserialize(key.substring(keyPrefixLength), rawResult));
      } else {
        results.add(null);
      }
    }

    return results;
  }


  @Override
  public List<T> queryByKeyPattern(@Nonnull T keyPatternQuery) {
    String keyPattern = getFullKey(keySerializerHelper.serializeKeyPattern(keyPatternQuery));
    final Set<String> keys = getRedisTemplate().keys(keyPattern);
    if (keys.isEmpty()) {
      return Collections.emptyList();
    } else {
      return getByKeyString(keys);
    }
  }

  @Override
  public List<T> queryByKeyPattern(@Nonnull String keyPatternString) {
    String fullKeyPatternString = getFullKey(keyPatternString);
    final Set<String> keys = getRedisTemplate().keys(fullKeyPatternString);
    if (keys.isEmpty()) {
      return Collections.emptyList();
    } else {
      return getByKeyString(keys);
    }
  }

}
