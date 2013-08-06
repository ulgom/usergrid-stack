package org.usergrid.rest.applications.collection.activities;

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
public class InQuerySyntaxTest extends RestContextTest {

  @Test
  public void inSyntax() throws Exception{

    CustomCollection activities = collection("activities");

    Map actor = hashMap("displayName", "Erin");
    Map props = new HashMap();
    props.put("actor", actor);
    props.put("content","bragh");

    for (int i = 0; i < 20; i++) {

      props.put("verb","go");
      props.put("ordinal", i);
      JsonNode activity = activities.create(props);
    }

    String query = "select * where verb in ('go')";
    JsonNode incorrectNode = activities.withQuery(query).get();

    assertEquals(20, incorrectNode.get("entities").size());

  }

  @Test
  public void inTwoSyntax() throws Exception{

    CustomCollection activities = collection("activities");

    Map actor = hashMap("displayName", "Erin");
    Map props = new HashMap();
    props.put("actor", actor);
    props.put("content","bragh");

    for (int i = 0; i < 20; i++) {

      if(i < 10)
        props.put("verb","go");
      else
        props.put("verb","stop");

      props.put("ordinal", i);
      JsonNode activity = activities.create(props);
    }

    String query = "select * where verb in ('go','stop')";
    JsonNode incorrectNode = activities.withQuery(query).get();

    assertEquals(20, incorrectNode.get("entities").size());

  }

  @Test
  public void inThreeSyntax() throws Exception{

    CustomCollection activities = collection("activities");

    Map actor = hashMap("displayName", "Erin");
    Map props = new HashMap();
    props.put("actor", actor);
    props.put("content","bragh");

    for (int i = 0; i < 30; i++) {

      if(i < 10)
        props.put("verb","go");
      else if (10 < i && i < 20)
        props.put("verb","stop");
      else
        props.put("verb","in motion");

      props.put("ordinal", i);
      JsonNode activity = activities.create(props);
    }

    String query = "select * where verb in ('go','stop','in motion')";
    JsonNode incorrectNode = activities.withQuery(query).get();

    assertEquals(30, incorrectNode.get("entities").size());

  }

  @Test
  public void inInterlacedSyntax() throws Exception{

    CustomCollection activities = collection("activities");

    Map actor = hashMap("displayName", "Erin");
    Map props = new HashMap();
    props.put("actor", actor);
    props.put("content","bragh");

    for (int i = 0; i < 30; i++) {

      if(i%2 == 0)
        props.put("verb","go");
      else if (i%3 == 0)
        props.put("verb","stop");
      else
        props.put("verb","in motion");

      props.put("ordinal", i);
      JsonNode activity = activities.create(props);
    }

    String query = "select * where verb in ('go','stop')";
    JsonNode incorrectNode = activities.withQuery(query).get();

    assertEquals(30, incorrectNode.get("entities").size());

  }

  @Test
  public void inBooleanSyntax() {

    CustomCollection activities = collection("activities");

    Map actor = hashMap("displayName", "Erin");
    Map props = new HashMap();
    props.put("actor", actor);
    props.put("content","bragh");

    for (int i = 0; i < 30; i++) {

      if(i < 15)
        props.put("verb",true);
      else
        props.put("verb",false);


      props.put("ordinal", i);
      JsonNode activity = activities.create(props);
    }

    String query = "select * where verb in (true)";
    JsonNode incorrectNode = activities.withQuery(query).get();

    assertEquals(15, incorrectNode.get("entities").size());

    query = "select * where verb in (false)";
    incorrectNode = activities.withQuery(query).get();

    assertEquals(15, incorrectNode.get("entities").size());

    query = "select * where verb in (true,false)";
    incorrectNode = activities.withQuery(query).get();

    assertEquals(30, incorrectNode.get("entities").size());

  }

  @Test
  public void inUUIDSyntax() {

    CustomCollection activities = collection("activities");



    Map actor = hashMap("displayName", "Erin");
    Map props = new HashMap();
    String[] uuids = new String[30];
    props.put("actor", actor);
    props.put("content","bragh");

    for (int i = 0; i < 30; i++) {

      if(i < 10)
        props.put("verb","go");
      else
        props.put("verb","stop");


      props.put("ordinal", i);
      JsonNode activity = activities.create(props);
      uuids[i] = activity.get("entities").get(0).get("uuid").getTextValue();
    }

    String query = "select * where uuid in ("+uuids[0]+","+uuids[1]+","+uuids[2]+")";
    JsonNode incorrectNode = activities.withQuery(query).get();

    assertEquals(3, incorrectNode.get("entities").size());
  }

  @Test
  public void inFloatSyntax() {

    CustomCollection activities = collection("activities");

    Map actor = hashMap("displayName", "Erin");
    Map props = new HashMap();
    props.put("actor", actor);
    props.put("content","bragh");

    for (int i = 0; i < 30; i++) {

      if(i < 15)
        props.put("verb",1.00001);
      else
        props.put("verb",2.04);


      props.put("ordinal", i);
      JsonNode activity = activities.create(props);
    }

    String query = "select * where verb in (1.00001)";
    JsonNode incorrectNode = activities.withQuery(query).get();

    assertEquals(15, incorrectNode.get("entities").size());

    query = "select * where verb in (2.04)";
    incorrectNode = activities.withQuery(query).get();

    assertEquals(15, incorrectNode.get("entities").size());

    query = "select * where verb in (1.00001,2.04)";
    incorrectNode = activities.withQuery(query).get();

    assertEquals(30, incorrectNode.get("entities").size());

  }
//  Won't compile due to hashmaps not taking things larger than integer values.
//  @Test
//  public void inLongSyntax() {
//
//    CustomCollection activities = collection("activities");
//
//    Map actor = hashMap("displayName", "Erin");
//    Map props = new HashMap();
//    props.put("actor", actor);
//    props.put("content","bragh");
//
//    for (int i = 0; i < 30; i++) {
//
//      if(i < 15)
//        props.put("verb",9223372036854775807);
//      else
//        props.put("verb",-9223372036854775807);
//
//
//      props.put("ordinal", i);
//      JsonNode activity = activities.create(props);
//    }
//
//    String query = "select * where verb in (9223372036854775807)";
//    JsonNode incorrectNode = activities.withQuery(query).get();
//
//    assertEquals(15, incorrectNode.get("entities").size());
//
//    query = "select * where verb in (-9223372036854775807)";
//    incorrectNode = activities.withQuery(query).get();
//
//    assertEquals(15, incorrectNode.get("entities").size());
//
//    query = "select * where verb in (9223372036854775807,-9223372036854775807)";
//    incorrectNode = activities.withQuery(query).get();
//
//    assertEquals(30, incorrectNode.get("entities").size());
//
//  }

  @Test
  public void inIntSyntax() {

    CustomCollection activities = collection("activities");

    Map actor = hashMap("displayName", "Erin");
    Map props = new HashMap();
    props.put("actor", actor);
    props.put("content","bragh");

    for (int i = 0; i < 30; i++) {

      if(i < 15)
        props.put("verb",1);
      else
        props.put("verb",2);


      props.put("ordinal", i);
      JsonNode activity = activities.create(props);
    }

    String query = "select * where verb in (1)";
    JsonNode incorrectNode = activities.withQuery(query).get();

    assertEquals(15, incorrectNode.get("entities").size());

    query = "select * where verb in (2)";
    incorrectNode = activities.withQuery(query).get();

    assertEquals(15, incorrectNode.get("entities").size());

    query = "select * where verb in (1,2)";
    incorrectNode = activities.withQuery(query).get();

    assertEquals(30, incorrectNode.get("entities").size());

  }
}
