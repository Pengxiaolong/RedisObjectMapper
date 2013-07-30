package org.opensource.redis.objectmapper.annotation;

import java.lang.annotation.*;

/**
 * if true, the repository will not save the key related property to Redis.
 * @author xlpeng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Parsimonious {
  boolean value() default true;
}
