package org.opensource.redis.objectmapper.domain;

import org.opensource.redis.objectmapper.annotation.Index;
import org.opensource.redis.objectmapper.annotation.Parsimonious;

/**
* Created with IntelliJ IDEA.
* User: xlpeng
* Date: 13-7-29
* Time: 下午11:42
* To change this template use File | Settings | File Templates.
*/
@Parsimonious(false)
public class Student extends Person{
  private String grade;

  @Index
  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }
}
