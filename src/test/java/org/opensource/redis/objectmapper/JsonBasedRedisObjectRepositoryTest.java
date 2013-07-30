package org.opensource.redis.objectmapper;

import com.google.common.collect.Sets;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensource.redis.objectmapper.domain.Person;
import org.opensource.redis.objectmapper.domain.Student;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * @author xlpeng
 */
public class JsonBasedRedisObjectRepositoryTest extends AbstractTest {
  static ApplicationContext applicationContext;
  static JsonBasedRedisObjectRepositoryFactory repositoryFactory;

  @BeforeClass
  public static void setup() {
    applicationContext = new ClassPathXmlApplicationContext("classpath:application-context.xml");
    repositoryFactory = applicationContext.getBean(JsonBasedRedisObjectRepositoryFactory.class);
  }

  @Test
  public void testSave() throws Exception {
    RedisObjectRepository<Person> redisObjectRepository = repositoryFactory.getRedisObjectRepository("test", Person.class);

    long start = System.currentTimeMillis();

    for (int i = 0; i < 1000; i++) {
      redisObjectRepository.save(person);
    }
    System.out.println(System.currentTimeMillis() - start);
    assertEquals(person, redisObjectRepository.getByKey(person));

    RedisObjectRepository<Student> studentRedisObjectRepository = repositoryFactory.getRedisObjectRepository("test", Student.class);
    for (int i = 0; i < 1000; i++) {
      studentRedisObjectRepository.save(student);
    }
    System.out.println(System.currentTimeMillis() - start);
    assertEquals(student, studentRedisObjectRepository.getByKey(student));
  }

  @Test
  public void testDeleteByKey() throws Exception {

  }

  @Test
  public void testDeleteByKeyPattern() throws Exception {

  }

  @Test
  public void testGetByKey() throws Exception {
    RedisObjectRepository<Person> personRedisObjectRepository = repositoryFactory.getRedisObjectRepository("test", Person.class);
    Person keyQuery = new Person();
    keyQuery.setId(person.getId());
    assertEquals(person, personRedisObjectRepository.getByKey(keyQuery));

    assertEquals(person, personRedisObjectRepository.getByKey("123"));
  }

  @Test
  public void testQueryByKeyPattern() throws Exception {
    RedisObjectRepository<Person> personRedisObjectRepository = repositoryFactory.getRedisObjectRepository("test", Person.class);
    Person keyQuery = new Person();
    keyQuery.setId(person.getId());
    assertEquals(Sets.newHashSet(person), new HashSet<>(personRedisObjectRepository.queryByKeyPattern(keyQuery)));

    assertEquals(Sets.newHashSet(person), new HashSet<>(personRedisObjectRepository.queryByKeyPattern("12*")));

  }
}