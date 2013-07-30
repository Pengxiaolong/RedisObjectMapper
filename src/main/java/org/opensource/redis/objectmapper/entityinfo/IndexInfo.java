package org.opensource.redis.objectmapper.entityinfo;

import org.opensource.redis.objectmapper.annotation.Index;

import java.beans.PropertyDescriptor;

/**
 * @author xlpeng
 */
public class IndexInfo {
  PropertyDescriptor property;
  String dateFormat;
  Index index;

  IndexInfo(Index index, PropertyDescriptor property) {
    this.index = index;
    this.property = property;
  }

  public Index getIndex() {
    return index;
  }

  public void setIndex(Index index) {
    this.index = index;
  }

  public PropertyDescriptor getProperty() {
    return property;
  }

  public void setProperty(PropertyDescriptor property) {
    this.property = property;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  @Override
  public String toString() {
    return "IndexInfo{" +
            "index=" + index +
            ", property=" + property +
            '}';
  }
}
