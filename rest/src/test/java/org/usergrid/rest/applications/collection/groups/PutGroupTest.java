package org.usergrid.rest.applications.collection.groups;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import org.usergrid.rest.RestContextTest;
import org.usergrid.rest.test.resource.CustomCollection;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.usergrid.utils.MapUtils.hashMap;

/**
 * // TODO: Document this
 *
 * @author ApigeeCorporation
 * @since 4.0
 */
public class PutGroupTest extends RestContextTest {

  @Test //USERGRID-1729
  public void  putMassUpdateTest () {

    CustomCollection activities = collection("groups");

    Map props = hashMap("test-app", "Erin");
    props.put("path","putMassUpdateTest");


    for (int i = 0; i < 1; i++) {

      props.put("ordinal", i);
      JsonNode activity = activities.create(props);
    }
    try {
    props.put("test-app","Joe");
    activities.put(props);
    }catch(UniformInterfaceException uie){
      assertEquals(200,uie.getResponse().getStatus());
      return;
    }

  }

}
