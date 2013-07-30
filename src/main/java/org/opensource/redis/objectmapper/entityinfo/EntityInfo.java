package org.opensource.redis.objectmapper.entityinfo;

import java.beans.PropertyDescriptor;
import java.util.List;

/**
 * @param <T>
 * @author xlpeng
 */
public class EntityInfo<T> {
  private Class<T> javaType;
  private String name;
  private boolean parsimoniousStorage;
  private List<IndexInfo> indexes;
  private List<KeyInfo> keyComponents;
  private List<PropertyDescriptor> properties;


  public EntityInfo(String name, boolean parsimoniousStorage, List<PropertyDescriptor> properties, List<KeyInfo> keyComponents, List<IndexInfo> indexes, Class<T> javaType) {
    this.name = name;
    this.parsimoniousStorage = parsimoniousStorage;
    this.properties = properties;
    this.keyComponents = keyComponents;
    this.indexes = indexes;
    this.javaType = javaType;
  }

  public List<IndexInfo> getIndexes() {
    return indexes;
  }

  public void setIndexes(List<IndexInfo> indexes) {
    this.indexes = indexes;
  }

  public Class<T> getJavaType() {
    return javaType;
  }

  public void setJavaType(Class<T> javaType) {
    this.javaType = javaType;
  }

  public List<KeyInfo> getKeyComponents() {
    return keyComponents;
  }

  public void setKeyComponents(List<KeyInfo> keyComponents) {
    this.keyComponents = keyComponents;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<PropertyDescriptor> getProperties() {
    return properties;
  }

  public void setProperties(List<PropertyDescriptor> properties) {
    this.properties = properties;
  }

  public boolean isParsimoniousStorage() {
    return parsimoniousStorage;
  }

  public void setParsimoniousStorage(boolean parsimoniousStorage) {
    this.parsimoniousStorage = parsimoniousStorage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    EntityInfo that = (EntityInfo) o;

    if (parsimoniousStorage != that.parsimoniousStorage) return false;
    if (indexes != null ? !indexes.equals(that.indexes) : that.indexes != null) return false;
    if (javaType != null ? !javaType.equals(that.javaType) : that.javaType != null) return false;
    if (keyComponents != null ? !keyComponents.equals(that.keyComponents) : that.keyComponents != null) return false;
    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = javaType != null ? javaType.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (parsimoniousStorage ? 1 : 0);
    result = 31 * result + (indexes != null ? indexes.hashCode() : 0);
    result = 31 * result + (keyComponents != null ? keyComponents.hashCode() : 0);
    result = 31 * result + (properties != null ? properties.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "EntityInfo{" +
            "indexes=" + indexes +
            ", javaType=" + javaType +
            ", name='" + name + '\'' +
            ", keyComponents=" + keyComponents +
            ", properties=" + properties +
            '}';
  }
}