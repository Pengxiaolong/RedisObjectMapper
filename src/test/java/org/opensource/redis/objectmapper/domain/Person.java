package org.opensource.redis.objectmapper.domain;

import org.opensource.redis.objectmapper.annotation.DateFormat;
import org.opensource.redis.objectmapper.annotation.Entity;
import org.opensource.redis.objectmapper.annotation.Index;
import org.opensource.redis.objectmapper.annotation.KeyComponent;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: xlpeng
 * Date: 13-7-29
 * Time: 下午11:42
 * To change this template use File | Settings | File Templates.
 */
@Entity()
public class Person {
  @KeyComponent(index = 1)
  private Long id;
  @Index
  private String name;
  @Index
  private String sex;
  @Index
  @DateFormat()
  private Date birthDay;

  public Date getBirthDay() {
    return birthDay;
  }

  public void setBirthDay(Date birthDay) {
    this.birthDay = birthDay;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Person person = (Person) o;

    if (id != person.id) return false;
    if (birthDay != null ? !birthDay.equals(person.birthDay) : person.birthDay != null) return false;
    if (name != null ? !name.equals(person.name) : person.name != null) return false;
    if (sex != null ? !sex.equals(person.sex) : person.sex != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (sex != null ? sex.hashCode() : 0);
    result = 31 * result + (birthDay != null ? birthDay.hashCode() : 0);
    return result;
  }
}
