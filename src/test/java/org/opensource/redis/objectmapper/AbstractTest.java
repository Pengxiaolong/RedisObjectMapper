package org.opensource.redis.objectmapper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.BeforeClass;
import org.opensource.redis.objectmapper.domain.Person;
import org.opensource.redis.objectmapper.domain.Student;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: xlpeng
 * Date: 13-7-29
 * Time: 下午11:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTest {
  protected static Person person;
  protected static Student student;
  @BeforeClass
  public static void beforeClass() throws ParseException, InvocationTargetException, IllegalAccessException {
    person = new Person();
    person.setId(123L);
    person.setName("Xiaolong, Peng");
    person.setSex("Man");
    person.setBirthDay(DateUtils.parseDate("1986-03-13", "yyyy-MM-dd"));

    student = new Student();
    BeanUtils.copyProperties(student, person);
    student.setGrade("200501");
  }
}
