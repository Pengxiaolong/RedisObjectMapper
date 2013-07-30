package org.opensource.redis.objectmapper.entityinfo;

import org.opensource.redis.objectmapper.annotation.KeyComponent;

import java.beans.PropertyDescriptor;

/**
* Created with IntelliJ IDEA.
* User: xlpeng
* Date: 13-7-28
* Time: 上午3:49
* To change this template use File | Settings | File Templates.
*/
public class KeyInfo {
  PropertyDescriptor property;
  KeyComponent keyComponent;
  String dateFormat;

  KeyInfo(KeyComponent keyComponent, PropertyDescriptor property) {
    this.keyComponent = keyComponent;
    this.property = property;
  }

  public KeyComponent getKeyComponent() {
    return keyComponent;
  }

  public void setKeyComponent(KeyComponent keyComponent) {
    this.keyComponent = keyComponent;
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
    return "KeyInfo{" +
            "dateFormat='" + dateFormat + '\'' +
            ", property=" + property +
            ", keyComponent=" + keyComponent +
            '}';
  }
}

