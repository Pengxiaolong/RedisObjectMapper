package org.opensource.redis.objectmapper;

import org.junit.Test;
import org.opensource.redis.objectmapper.domain.Person;
import org.opensource.redis.objectmapper.domain.Student;
import org.opensource.redis.objectmapper.entityinfo.EntityInfo;
import org.opensource.redis.objectmapper.entityinfo.EntityInfoFactory;

import static org.junit.Assert.*;


/**
 * @author xlpeng
 */
public class EntityInfoFactoryTest {
  @Test
  public void testGetEntityInfo() throws Exception {
    EntityInfo<Person> personEntityInfo = EntityInfoFactory.getEntityInfo(Person.class);
    assertNotNull(personEntityInfo);
    assertEquals(personEntityInfo.getJavaType(), Person.class);
    assertEquals(personEntityInfo.getKeyComponents().size(), 1);
    assertEquals(personEntityInfo.getKeyComponents().get(0).getProperty().getName(), "id");
    assertEquals(personEntityInfo.getProperties().size(), 4);
    assertEquals(3, personEntityInfo.getIndexes().size());
    assertTrue(personEntityInfo.isParsimoniousStorage());
    assertEquals(personEntityInfo.getName(), "Person");

    EntityInfo<Student> studentEntityInfo = EntityInfoFactory.getEntityInfo(Student.class);
    assertNotNull(studentEntityInfo);
    assertEquals(studentEntityInfo.getJavaType(), Student.class);
    assertEquals(studentEntityInfo.getKeyComponents().size(), 1);
    assertEquals(studentEntityInfo.getKeyComponents().get(0).getProperty().getName(), "id");
    assertEquals(studentEntityInfo.getProperties().size(), 5);
    assertEquals(studentEntityInfo.getIndexes().size(), 4);
    assertTrue(!studentEntityInfo.isParsimoniousStorage());
    assertEquals(studentEntityInfo.getName(), "Student");
  }

}
