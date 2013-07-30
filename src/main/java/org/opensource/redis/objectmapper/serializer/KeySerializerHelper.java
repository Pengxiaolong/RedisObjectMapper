package org.opensource.redis.objectmapper.serializer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.opensource.redis.objectmapper.entityinfo.EntityInfo;
import org.opensource.redis.objectmapper.entityinfo.EntityInfoFactory;
import org.opensource.redis.objectmapper.entityinfo.KeyInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

/**
 * @author xlpeng
 */
public class KeySerializerHelper {

  public String serializeKey(Object object) {
    try {
      EntityInfo<?> entityInfo = EntityInfoFactory.getEntityInfo(object.getClass());
      List<KeyInfo> keyInfos = entityInfo.getKeyComponents();
      Object[] keyComponents = new Object[keyInfos.size()];

      for (int i = 0; i < keyInfos.size(); i++) {
        keyComponents[i] = getKeyComponentValue(keyInfos.get(i), object);
      }

      return StringUtils.join(keyComponents, ":");
    } catch (InvocationTargetException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  public String serializeKeyPattern(Object object) {
    try {
      EntityInfo<?> entityInfo = EntityInfoFactory.getEntityInfo(object.getClass());
      List<KeyInfo> keyInfos = entityInfo.getKeyComponents();
      Object[] keyComponents = new Object[keyInfos.size()];

      for (int i = 0; i < keyInfos.size(); i++) {
        Object keyComponentValue = getKeyComponentValue(keyInfos.get(i), object);
        keyComponents[i] = (keyComponentValue == null ? "*" : keyComponentValue);
      }

      return StringUtils.join(keyComponents, ":");
    } catch (InvocationTargetException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
  }

  public <E> E deserializeKey(String key, EntityInfo<E> entityInfo) {
    String[] keyComponents = key.split(":");
    List<KeyInfo> keyInfos = entityInfo.getKeyComponents();

    assert keyComponents.length == keyInfos.size();

    try {
      E entity = entityInfo.getJavaType().newInstance();
      for (int i = 0; i < keyComponents.length; i++) {
        KeyInfo keyInfo = keyInfos.get(i);
        if (keyInfo.getProperty().getPropertyType() != Date.class) {
          keyInfo.getProperty().getWriteMethod().invoke(entity, StringConverter.convertFromString(keyComponents[i], keyInfo.getProperty().getPropertyType()));
        }
      }
      return entity;
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static <T> Object getKeyComponentValue(KeyInfo keyInfo, T entity) throws InvocationTargetException, IllegalAccessException {
    Object propertyValue = keyInfo.getProperty().getReadMethod().invoke(entity);
    if (keyInfo.getProperty().getPropertyType() == Date.class && propertyValue != null) {
      propertyValue = DateFormatUtils.format((Date) propertyValue, keyInfo.getDateFormat());
    }
    return propertyValue;
  }

}
