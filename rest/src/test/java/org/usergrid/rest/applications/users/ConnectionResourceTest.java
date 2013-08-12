package org.usergrid.rest.applications.users;

import com.sun.jersey.api.client.UniformInterfaceException;
import org.codehaus.jackson.JsonNode;
import org.usergrid.rest.RestContextTest;

import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;

import org.junit.Test;
import org.junit.Ignore;
import com.sun.jersey.api.client.ClientResponse;
import org.usergrid.rest.test.resource.app.CustomEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.usergrid.utils.MapUtils.hashMap;

import org.usergrid.rest.test.resource.CustomCollection;

/**
 * // TODO: Document this
 *
 * @author ApigeeCorporation
 * @since 4.0
 */
public class ConnectionResourceTest extends RestContextTest {

  @Test
     public void connectionsQueryTest() {

    CustomEntity items = new CustomEntity("item", null);

    CustomCollection activities = collection("peeps");

    Map stuff = hashMap("type", "chicken");

    activities.create(stuff);


    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("username", "todd");

    Map<String, Object> objectOfDesire = new LinkedHashMap<String, Object>();
    objectOfDesire.put("codingmunchies", "doritoes");

    JsonNode node = resource().path("/test-organization/test-app/users")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

    payload.put("username", "scott");


    node = resource().path("/test-organization/test-app/users")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);
    /*finish setting up the two users */


    ClientResponse toddWant = resource().path("/test-organization/test-app/users/todd/likes/peeps")
        .queryParam("access_token", access_token)
        .accept(MediaType.TEXT_HTML)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(ClientResponse.class, objectOfDesire);

    assertEquals(200, toddWant.getStatus());

    node = resource().path("/test-organization/test-app/peeps")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    String uuid = node.get("entities").get(0).get("uuid").getTextValue();




    try {
      node = resource().path("/test-organization/test-app/users/scott/likes/" + uuid)
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .get(JsonNode.class);
      assert (false);
    } catch (UniformInterfaceException uie) {
      assertEquals(404, uie.getResponse().getClientResponseStatus().getStatusCode());
      return;
    }

  }

  /*change things so that they say item instead of peeps*/
  @Test
  public void realDeleteQueryTest() {

   // CustomEntity items = new CustomEntity("item", null);

    CustomCollection activities = collection("peeps");

    Map stuff = hashMap("type", "chicken");

    activities.create(stuff);


    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("username", "todd");

    Map<String, Object> objectOfDesire = new LinkedHashMap<String, Object>();
    objectOfDesire.put("codingmunchies", "doritoes");

    JsonNode node = resource().path("/test-organization/test-app/users")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

    payload.put("username", "scott");


    node = resource().path("/test-organization/test-app/users")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);
    /*finish setting up the two users */


    ClientResponse toddWant = resource().path("/test-organization/test-app/users/me/owns/peeps")
        .queryParam("access_token", access_token)
        .accept(MediaType.TEXT_HTML)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(ClientResponse.class, objectOfDesire);

    assertEquals(200, toddWant.getStatus());

    node = resource().path("/test-organization/test-app/peeps")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    String uuid = node.get("entities").get(0).get("uuid").getTextValue();




    try {
      node = resource().path("/test-organization/test-app/users/me/owns/" + uuid)
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .get(JsonNode.class);
      //assert (false);
    } catch (UniformInterfaceException uie) {
      assertEquals(404, uie.getResponse().getClientResponseStatus().getStatusCode());
      return;
    }



    node = resource().path("/test-organization/test-app/users/me/owns/peeps/" + uuid)
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .delete(JsonNode.class);

    //assert(false);


  }

  /*change things so that they say item instead of peeps*/
  @Ignore("still doesn't represent dino's unauth access issue ")   //USERGRID-1713
  public void dinoDeleteQueryTest() {

    // CustomEntity items = new CustomEntity("item", null);

    CustomCollection activities = collection("items");

    //Map stuff = hashMap("type", "chicken");

    //activities.create(stuff);


    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("username", "todd");

    Map<String, Object> objectOfDesire = new LinkedHashMap<String, Object>();
    objectOfDesire.put("codingmunchies", "doritoes");

    JsonNode node = resource().path("/test-organization/test-app/users")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

    payload.put("username", "scott");



    node = resource().path("/test-organization/test-app/users")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

//    payload.put("password","1234");
//
//    node= resource().path("/test-organization/test-app/token")
//        //.queryParam("access_token", access_token)
//        .accept(MediaType.APPLICATION_JSON)
//        .type(MediaType.APPLICATION_JSON_TYPE)
//        .get(JsonNode.class);
    /*finish setting up the two users */


    ClientResponse toddWant = resource().path("/test-organization/test-app/users/me/owns/items")
        .queryParam("access_token", access_token)
        .accept(MediaType.TEXT_HTML)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(ClientResponse.class, objectOfDesire);

    assertEquals(200, toddWant.getStatus());

    node = resource().path("/test-organization/test-app/items")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    String uuid = node.get("entities").get(0).get("uuid").getTextValue();




    try {
      node = resource().path("/test-organization/test-app/users/me/owns/" + uuid)
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .get(JsonNode.class);
      //assert (false);
    } catch (UniformInterfaceException uie) {
      assertEquals(200, uie.getResponse().getClientResponseStatus().getStatusCode());
      return;
    }



    node = resource().path("/test-organization/test-app/users/me/owns/items/" + uuid)
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .delete(JsonNode.class);

    try {
      node = resource().path("/test-organization/test-app/users/me/owns/" + uuid)
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .get(JsonNode.class);
      //assert (false);
    } catch (UniformInterfaceException uie) {
      assertEquals(404, uie.getResponse().getClientResponseStatus().getStatusCode());
      return;
    }

    try {
      node = resource().path("/test-organization/test-app/items/" + uuid)
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .get(JsonNode.class);
      //assert (false);
    } catch (UniformInterfaceException uie) {
      assertEquals(200, uie.getResponse().getClientResponseStatus().getStatusCode());
      return;
    }

    //assert(false);


  }


//  @Test
//  public void deleteConnectionsTest () {
//
//    CustomEntity items = new CustomEntity("item",null);
//
//    Map<String, Object> payload = new LinkedHashMap<String, Object>();
//    payload.put("uuid", "59129360-fa30-11e2-bbf5-bbb7a60289dc");
//
//
//    JsonNode node = resource().path("/test-organization/test-app/users/me/owns/items")
//        .queryParam("access_token", access_token)
//        .accept(MediaType.APPLICATION_JSON)
//        .type(MediaType.APPLICATION_JSON_TYPE)
//        .post(JsonNode.class,payload);
//
//    String uuid = node.get("entities").get(0).get("uuid").getTextValue();
//    node = resource().path("/test-organization/test-app/users/me/owns/items")
//        .queryParam("access_token", access_token)
//        .accept(MediaType.APPLICATION_JSON)
//        .type(MediaType.APPLICATION_JSON_TYPE)
//        .get(JsonNode.class);
//
//    assertNotNull(node.get("entities").get(0));
//
//    ClientResponse deleteResponse = resource().path("/test-organization/test-app/users/me/owns/items/"+uuid)
//        .queryParam("access_token", access_token)
//        .accept(MediaType.TEXT_HTML)
//        .type(MediaType.APPLICATION_JSON_TYPE)
//        .delete(ClientResponse.class);
//
//    assertEquals(200,deleteResponse.getStatus());
//
//  }



}
