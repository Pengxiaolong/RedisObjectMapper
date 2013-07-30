package org.opensource.redis.objectmapper;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensource.redis.objectmapper.domain.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: xlpeng
 * Date: 13-7-30
 * Time: 上午2:07
 * To change this template use File | Settings | File Templates.
 */

public class JsonBasedRedisObjectRepositoryTest extends AbstractTest {
  static ApplicationContext applicationContext;
  static JsonBasedRedisObjectRepositoryFactory repositoryFactory ;
  @BeforeClass
  public static void setup(){
    applicationContext = new ClassPathXmlApplicationContext("classpath:application-context.xml");
    repositoryFactory = applicationContext.getBean(JsonBasedRedisObjectRepositoryFactory.class);
  }

  @Test
  public void testSave() throws Exception {
    repositoryFactory.getRedisObjectRepository("test", Person.class).save(person);
    assertEquals(person, repositoryFactory.getRedisObjectRepository("test", Person.class).getByKey(person));
  }

  @Test
  public void testGetFullKey() throws Exception {

  }

  @Test
  public void testDeleteByKey() throws Exception {

  }

  @Test
  public void testDeleteByKeyPattern() throws Exception {

  }

  @Test
  public void testGetByKey() throws Exception {

  }

  @Test
  public void testQueryByKeyPattern() throws Exception {

  }
}