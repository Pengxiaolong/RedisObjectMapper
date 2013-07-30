package org.opensource.redis.objectmapper.entityinfo;

import org.apache.commons.beanutils.PropertyUtils;
import org.opensource.redis.objectmapper.Tris;
import org.opensource.redis.objectmapper.annotation.*;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xlpeng
 *         EntityInfo Factory, it create an instance of {@link org.opensource.redis.objectmapper.entityinfo.EntityInfo} by parsing annotation on class.
 * @author xlpeng
 */
public class EntityInfoFactory {

  private static Map<Class<?>, EntityInfo<?>> entityInfoMap = new ConcurrentHashMap<>();

  /**
   * get instance of {@link EntityInfo}.
   *
   * @param javaType java type
   * @return   instance of EntityInfo
   */
  @SuppressWarnings("unchecked")
  public static <T> EntityInfo<T> getEntityInfo(Class<T> javaType) {
    assert javaType != null;

    if (!entityInfoMap.containsKey(javaType)) {
      processEntityInfo(javaType);
    }
    return (EntityInfo<T>) entityInfoMap.get(javaType);
  }

  private static <T> void processEntityInfo(Class<T> javaType) {
    if (javaType.isAnnotationPresent(Entity.class)) {
      String entityName = getEntityName(javaType);
      boolean parsimoniousStorage = !javaType.isAnnotationPresent(Parsimonious.class) || javaType.getAnnotation(Parsimonious.class).value();

      Tris<List<KeyInfo>, List<IndexInfo>, List<PropertyDescriptor>> tris = getKeyAndIndexInfo(javaType);
      checkAndSortKeys(tris.getFirst(), entityName);
      entityInfoMap.put(javaType, new EntityInfo<>(entityName,
              parsimoniousStorage,
              Collections.unmodifiableList(tris.getThird()),
              Collections.unmodifiableList(tris.getFirst()),
              Collections.unmodifiableList(tris.getSecond()),
              javaType));
    } else {
      throw new RuntimeException("class " + javaType.getName() + " is not an entity.");
    }
  }

  private static void checkAndSortKeys(List<KeyInfo> keys, String entityName) {
    if (keys.isEmpty()) {
      throw new RuntimeException("Key components configuration of entity " + entityName + " is required.");
    }
    Collections.sort(keys, new Comparator<KeyInfo>() {
      @Override
      public int compare(KeyInfo first, KeyInfo second) {
        if (first.keyComponent.index() != second.keyComponent.index()) {
          return first.keyComponent.index() - second.keyComponent.index();
        } else {
          return first.property.getName().compareTo(second.property.getName());
        }
      }
    });
  }

  private static <T> Tris<List<KeyInfo>, List<IndexInfo>, List<PropertyDescriptor>> getKeyAndIndexInfo(Class<T> javaType) {
    List<KeyInfo> keys = new ArrayList<>();
    List<IndexInfo> indexes = new ArrayList<>();
    List<PropertyDescriptor> properties = new ArrayList<>();
    Map<String, PropertyDescriptor> propertyDescriptorMap = getProperties(javaType);

    for (Map.Entry<String, PropertyDescriptor> propertyDescriptorEntry : propertyDescriptorMap.entrySet()) {
      properties.add(propertyDescriptorEntry.getValue());
      KeyComponent keyComponent = getAnnotationOnProperty(propertyDescriptorEntry.getValue(), javaType, KeyComponent.class);
      Index index = getAnnotationOnProperty(propertyDescriptorEntry.getValue(), javaType, Index.class);

      if (keyComponent != null) {
        KeyInfo keyInfo = new KeyInfo(keyComponent, propertyDescriptorEntry.getValue());
        if (propertyDescriptorEntry.getValue().getPropertyType() == Date.class) {
          DateFormat dateFormat = getAnnotationOnProperty(propertyDescriptorEntry.getValue(), javaType, DateFormat.class);
          keyInfo.setDateFormat(dateFormat == null ? "yyyyMMdd" : dateFormat.value());
        }
        keys.add(keyInfo);
      }

      if (index != null) {
        IndexInfo indexInfo = new IndexInfo(index, propertyDescriptorEntry.getValue());
        if (propertyDescriptorEntry.getValue().getPropertyType() == Date.class) {
          DateFormat dateFormat = getAnnotationOnProperty(propertyDescriptorEntry.getValue(), javaType, DateFormat.class);
          indexInfo.setDateFormat(dateFormat == null ? "yyyyMMdd" : dateFormat.value());
        }
        indexes.add(indexInfo);
      }
    }

    return new Tris<>(keys, indexes, properties);
  }

  /**
   * @param propertyDescriptor PropertyDescriptor
   * @param <A>                annotation type
   * @return instance of annotationType
   */
  private static <A extends Annotation> A getAnnotationOnProperty(PropertyDescriptor propertyDescriptor, Class<?> javaType, Class<A> annotationType) {
    Method setter = propertyDescriptor.getWriteMethod();
    Method getter = propertyDescriptor.getReadMethod();
    if (setter.isAnnotationPresent(annotationType) && getter.isAnnotationPresent(annotationType)) {
      throw new RuntimeException("Annotation " + annotationType.getSimpleName() + " cannot be annotated on both setter and getter method.");
    }
    A annotation = getter.isAnnotationPresent(annotationType) ? getter.getAnnotation(annotationType) : setter.getAnnotation(annotationType);
    if (annotation == null) {
      Field filed = findDeclaredFiled(javaType, propertyDescriptor.getName());
      if (filed != null) {
        if (filed.isAnnotationPresent(annotationType)) {
          annotation = filed.getAnnotation(annotationType);
        }
      }
    }
    return annotation;
  }

  private static Field findDeclaredFiled(Class javaType, String fileName) {
    Field field = null;
    try {
      field = javaType.getDeclaredField(fileName);
    } catch (NoSuchFieldException e) {
      //ignore
    }
    return field != null || javaType.getSuperclass().equals(Object.class) ? field : findDeclaredFiled(javaType.getSuperclass(), fileName);
  }


  private static Map<String, PropertyDescriptor> getProperties(Class<?> javaType) {
    Map<String, PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
    for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(javaType)) {
      if (propertyDescriptor.getName().equals("class")) {
        continue;
      }
      if (propertyDescriptor.getReadMethod() == null || propertyDescriptor.getReadMethod() == null) {
        throw new RuntimeException("Both setter and getter of property " + propertyDescriptor.getName() + " are required.");
      }
      propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
    }
    return propertyDescriptorMap;
  }

  private static String getEntityName(Class<?> javaType) {
    String entityName = javaType.getAnnotation(Entity.class).name().trim();
    return entityName.equals(Entity.USE_CLASS_NAME_AS_DEFAULT_ENTITY_NAME) || entityName.length() == 0 ? javaType.getSimpleName() : entityName;
  }
}
