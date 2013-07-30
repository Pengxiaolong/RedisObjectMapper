package org.opensource.redis.objectmapper.serializer;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.opensource.redis.objectmapper.Tris;
import org.opensource.redis.objectmapper.entityinfo.EntityInfo;
import org.opensource.redis.objectmapper.entityinfo.KeyInfo;
import org.opensource.redis.objectmapper.index.IndexHelper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xlpeng
 */
public class JsonBasedEntitySerializer<T> implements EntitySerializer<T, String> {
  private static ObjectMapper objectMapper = new ObjectMapper();
  private EntityInfo<T> entityInfo;
  KeySerializerHelper keySerializerHelper = new KeySerializerHelper();

  static {
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
  }

  private JsonBasedEntitySerializer(EntityInfo<T> entityInfo) {
    this.entityInfo = entityInfo;
  }

  private static Map<EntityInfo<?>, JsonBasedEntitySerializer> cachedEntitySerializers = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public static <T> JsonBasedEntitySerializer<T> getEntitySerializer(EntityInfo<T> entityInfo) {
    if (!cachedEntitySerializers.containsKey(entityInfo)) {
      cachedEntitySerializers.put(entityInfo, new JsonBasedEntitySerializer(entityInfo));
    }
    return (JsonBasedEntitySerializer<T>) cachedEntitySerializers.get(entityInfo);
  }

  @Override
  public Tris<String, String, Collection<String>> serialize(T t) {
    try {
      String jsonData = objectMapper.writer().withFilters(buildFilter(entityInfo)).writeValueAsString(t);
      String key = keySerializerHelper.serializeKey(t);
      Collection<String> indexesKeys;
      if (entityInfo.getIndexes().isEmpty()) {
        indexesKeys = Collections.emptyList();
      } else {
        indexesKeys = IndexHelper.buildIndexKeys(t);
      }
      return new Tris<>(key, jsonData, indexesKeys);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static final Map<Class<?>, SimpleFilterProvider> filterProviders = new ConcurrentHashMap<>();

  private FilterProvider buildFilter(EntityInfo<T> entityInfo) {
    if (!filterProviders.containsKey(entityInfo.getClass())) {
      SimpleFilterProvider filterProvider = new SimpleFilterProvider();
      Set<String> properties = new HashSet<>();
      //if is parsimonious storage, filter out all the key component fields.
      if (entityInfo.isParsimoniousStorage()) {
        for (KeyInfo keyInfo : entityInfo.getKeyComponents()) {
          if(keyInfo.getProperty().getPropertyType() != Date.class)
            properties.add(keyInfo.getProperty().getName());
        }
      }

      properties.add("class");
      filterProvider.addFilter("excludes", SimpleBeanPropertyFilter.serializeAllExcept(properties));
      filterProviders.put(entityInfo.getClass(), filterProvider);
    }
    return filterProviders.get(entityInfo.getClass());
  }

  @Override
  public T deserialize(String key, String jsonData) {
    try {
      T ret = objectMapper.readValue(jsonData, entityInfo.getJavaType());
      T keyRet = keySerializerHelper.deserializeKey(key, entityInfo);
      for (KeyInfo keyInfo : entityInfo.getKeyComponents()) {
        keyInfo.getProperty().getWriteMethod().invoke(ret, keyInfo.getProperty().getReadMethod().invoke(keyRet));
      }
      return ret;
    } catch (IOException | InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }


}
