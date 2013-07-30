package org.opensource.redis.objectmapper.annotation;


import java.lang.annotation.*;

/**
 * @author  xlpeng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Entity {
  public static final String USE_CLASS_NAME_AS_DEFAULT_ENTITY_NAME = "__class_name__";
  String name() default USE_CLASS_NAME_AS_DEFAULT_ENTITY_NAME;
}