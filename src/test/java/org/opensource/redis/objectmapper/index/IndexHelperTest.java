package org.opensource.redis.objectmapper.index;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.opensource.redis.objectmapper.AbstractTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class IndexHelperTest extends AbstractTest {
  @Test
  public void testBuildIndexKeys() throws Exception {
    Set<String> indexKeys = new HashSet<>();
    indexKeys.add(StringUtils.join(new Object[]{"Person", IndexHelper.indexKeyPrefix, "name", person.getName()}, ":"));
    indexKeys.add(StringUtils.join(new Object[]{"Person", IndexHelper.indexKeyPrefix, "birthDay", "19860313"}, ":"));
    indexKeys.add(StringUtils.join(new Object[]{"Person", IndexHelper.indexKeyPrefix, "sex", "Man"}, ":"));

    assertEquals(indexKeys, new HashSet<>(IndexHelper.buildIndexKeys(person)));

    HashSet<String> indexKeys2 = new HashSet<>();
    for (String key : indexKeys) {
      indexKeys2.add(key.replace("Person", "Student"));
    }
    indexKeys2.add(StringUtils.join(new Object[]{"Student", IndexHelper.indexKeyPrefix, "grade", student.getGrade()}, ":"));

    assertEquals(indexKeys2, new HashSet<>(IndexHelper.buildIndexKeys(student)));
  }
}
