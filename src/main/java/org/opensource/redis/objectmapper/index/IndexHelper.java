package org.opensource.redis.objectmapper.index;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.opensource.redis.objectmapper.entityinfo.EntityInfo;
import org.opensource.redis.objectmapper.entityinfo.EntityInfoFactory;
import org.opensource.redis.objectmapper.entityinfo.IndexInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author xlpeng
 */
public class IndexHelper {
  public static final String indexKeyPrefix = "_idx_";

  public static <T> Collection<String> buildIndexKeys(T entity) {
    EntityInfo<T> entityInfo = EntityInfoFactory.getEntityInfo((Class<T>) entity.getClass());
    if (entityInfo.getIndexes().isEmpty()) {
      return Collections.emptySet();
    }
    Set<String> indexKeys = new HashSet<>(entityInfo.getIndexes().size());
    try {
      for (IndexInfo indexInfo : entityInfo.getIndexes()) {
        indexKeys.add(StringUtils.join(new Object[]{indexKeyPrefix, indexInfo.getProperty().getName(), getIndexPropertyValue(indexInfo, entity)}, ":"));
      }
      return indexKeys;
    } catch (InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static <T> Object getIndexPropertyValue(IndexInfo indexInfo, T entity) throws InvocationTargetException, IllegalAccessException {
    Object propertyValue = indexInfo.getProperty().getReadMethod().invoke(entity);
    if (indexInfo.getProperty().getPropertyType() == Date.class && propertyValue != null) {
      propertyValue = DateFormatUtils.format((Date) propertyValue, indexInfo.getDateFormat());
    }
    return propertyValue;
  }
}
