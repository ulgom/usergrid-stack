package org.usergrid.rest.applications.users;

import org.codehaus.jackson.JsonNode;
import org.usergrid.rest.RestContextTest;

import javax.ws.rs.core.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import com.sun.jersey.api.client.ClientResponse;
import org.usergrid.rest.test.resource.app.CustomEntity;


/**
 * // TODO: Document this
 *
 * @author ApigeeCorporation
 * @since 4.0
 */
public class EntityResourceTest extends RestContextTest {

  @Test
  public void deleteConnectionsTest () {

    CustomEntity items = new CustomEntity("item",null);

    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("uuid", "59129360-fa30-11e2-bbf5-bbb7a60289dc");


    JsonNode node = resource().path("/test-organization/test-app/users/me/owns/items")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class,payload);//, payload);

    String uuid = node.get("entities").get(0).get("uuid").getTextValue();
    node = resource().path("/test-organization/test-app/users/me/owns/items")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    ClientResponse deleteResponse = resource().path("/test-organization/test-app/users/me/owns/items/"+uuid)
        .queryParam("access_token", access_token)
        .accept(MediaType.TEXT_HTML)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .delete(ClientResponse.class);

    System.out.println("hihi");



  }

}
