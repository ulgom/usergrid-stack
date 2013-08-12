package org.usergrid.rest.applications.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usergrid.rest.AbstractRestTest;

public class EventsResourceTest extends AbstractRestTest {

	private static Logger log = LoggerFactory
			.getLogger(EventsResourceTest.class);

	@Test
	public void testEventPostandGet() {

		Map<String, Object> payload = new LinkedHashMap<String, Object>();
		payload.put("timestamp", 0);
		payload.put("category", "advertising");
		payload.put("counters", new LinkedHashMap<String, Object>() {
			{
				put("ad_clicks", 5);
			}
		});

		JsonNode node = resource().path("/test-organization/test-app/events")
				.queryParam("access_token", access_token)
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.post(JsonNode.class, payload);

		assertNotNull(node.get("entities"));
		String advertising = node.get("entities").get(0).get("uuid").asText();

		payload = new LinkedHashMap<String, Object>();
		payload.put("timestamp", 0);
		payload.put("category", "sales");
		payload.put("counters", new LinkedHashMap<String, Object>() {
			{
				put("ad_sales", 20);
			}
		});

		node = resource().path("/test-organization/test-app/events")
				.queryParam("access_token", access_token)
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.post(JsonNode.class, payload);

		assertNotNull(node.get("entities"));
		String sales = node.get("entities").get(0).get("uuid").asText();

		payload = new LinkedHashMap<String, Object>();
		payload.put("timestamp", 0);
		payload.put("category", "marketing");
		payload.put("counters", new LinkedHashMap<String, Object>() {
			{
				put("ad_clicks", 10);
			}
		});

		node = resource().path("/test-organization/test-app/events")
				.queryParam("access_token", access_token)
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.post(JsonNode.class, payload);

		assertNotNull(node.get("entities"));
		String marketing = node.get("entities").get(0).get("uuid").asText();

		String lastId = null;
		
		// subsequent GETs advertising
		for (int i = 0; i < 3; i++) {

			node = resource().path("/test-organization/test-app/events")
					.queryParam("access_token", access_token)
					.accept(MediaType.APPLICATION_JSON)
					.type(MediaType.APPLICATION_JSON_TYPE).get(JsonNode.class);

			logNode(node);
			assertEquals("Expected Advertising", advertising, node.get("messages").get(0).get("uuid").asText());
			lastId = node.get("last").asText();
		}

		// check sales event in queue
		node = resource().path("/test-organization/test-app/events").queryParam("last", lastId)
				.queryParam("access_token", access_token)
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE).get(JsonNode.class);

		logNode(node);
		assertEquals("Expected Sales", sales,node.get("messages").get(0).get("uuid").asText());
		lastId = node.get("last").asText();
		

		// check marketing event in queue
		node = resource().path("/test-organization/test-app/events").queryParam("last", lastId)
				.queryParam("access_token", access_token)
				.accept(MediaType.APPLICATION_JSON)
				.type(MediaType.APPLICATION_JSON_TYPE).get(JsonNode.class);

		logNode(node);
		assertEquals("Expected Marketing", marketing, node.get("messages").get(0).get("uuid").asText());


	}

  @Test
  public void testCounterCreations() {

    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("timestamp", 0);
    payload.put("category", "testing");
    payload.put("counters", new LinkedHashMap<String, Object>() {
      {
        put("created", 2);
      }
    });

    JsonNode node = resource().path("/test-organization/test-app/events")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

    assertNotNull(node.get("entities"));

    JsonNode rest = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","created")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals("created",rest.get("counters").get(0).get("name").getTextValue());
    assertEquals(2,rest.get("counters").get(0).get("values").get(0).get("value").getIntValue());

  }

  @Test
  public void testCounterCreations1000() {


    JsonNode node = null;
    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("timestamp", 0);
    payload.put("category", "testing");

    for(int index = 0; index<1000;index++) {

      payload.put("counters", new LinkedHashMap<String, Object>() {

        {
          put("created1"+System.currentTimeMillis(), 1);
        }
      });

       node = resource().path("/test-organization/test-app/events")
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .post(JsonNode.class, payload);

    }

    JsonNode rest = resource().path("/test-organization/test-app/counters")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals(1009,rest.get("data").size());

  }

  @Test
  public void testCounterCreations5000() {


    JsonNode node = null;
    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("timestamp", 0);
    payload.put("category", "testing");

    for(int index = 0; index<5000;index++) {

      payload.put("counters", new LinkedHashMap<String, Object>() {

        {
          put("created2"+System.currentTimeMillis(), 1);
        }
      });

      node = resource().path("/test-organization/test-app/events")
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .post(JsonNode.class, payload);

    }

    JsonNode rest = resource().path("/test-organization/test-app/counters")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals(5009,rest.get("data").size());

  }

  @Test
  public void testCounterCreations1000000() {


    JsonNode node = null;
    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("timestamp", 0);
    payload.put("category", "testing");

    for(int index = 0; index<1000000;index++) {

      payload.put("counters", new LinkedHashMap<String, Object>() {

        {
          put("created3"+System.currentTimeMillis(), 1);
        }
      });

      node = resource().path("/test-organization/test-app/events")
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .post(JsonNode.class, payload);

    }

    JsonNode rest = resource().path("/test-organization/test-app/counters")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals(1000009,rest.get("data").size());

  }

  @Test
  public void testCounterAddition() {

    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("timestamp", 0);
    payload.put("category", "testing");
    payload.put("counters", new LinkedHashMap<String, Object>() {
      {
        put("useless_clicks", 2);
      }
    });

    JsonNode node = resource().path("/test-organization/test-app/events")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

    assertNotNull(node.get("entities"));

    JsonNode rest = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","useless_clicks")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals("useless_clicks",rest.get("counters").get(0).get("name").getTextValue());
   assertEquals(2,rest.get("counters").get(0).get("values").get(0).get("value").getIntValue());
   //JsonNode rest = null;

    for(int i = 0; i < 1000; i++) {
      node = resource().path("/test-organization/test-app/events")
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .post(JsonNode.class, payload);
    }

    rest = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","useless_clicks")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals(2002,rest.get("counters").get(0).get("values").get(0).get("value").getIntValue());

    for(int i = 0; i < 1000; i++) {
      node = resource().path("/test-organization/test-app/events")
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .post(JsonNode.class, payload);
    }

    rest = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","useless_clicks")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals(4002,rest.get("counters").get(0).get("values").get(0).get("value").getIntValue());

  }
  /*
  There appears to be 3 ways to get the count of a collection:
1. Do a get on the whole collection with a limit (only works up to 999). This returns 21:
GET https://api.usergrid.com/1hotrod/sandbox/users?limit=999
2. Do a get on the application. This returns 43:
GET https://api.usergrid.com/1hotrod/sandbox/
3. Do a get using the counter. This returns 16:
https://api.usergrid.com/1hotrod/sandbox/counters?counter=application.collection.users
The correct number is 21.

   */

  @Test
  public void collectionGet() {


    Map userPayload = new LinkedHashMap<String, Object>();

    for(int i = 0; i < 20;i++) {
      userPayload.put("username", "bob"+System.currentTimeMillis());

      JsonNode node = resource().path("/test-organization/test-app/user")
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .post(JsonNode.class, userPayload);
    }

    JsonNode node = resource().path("/test-organization/test-app/user")
        .queryParam("limit","999")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    int collectGet = node.get("entities").size();

    node = resource().path("/test-organization/test-app")
        .queryParam("limit","999")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    int regularGet = node.get("entities").size();

    node = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","application.collection.users")
        .queryParam("limit","999")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    int superget = node.get("entities").size();
    assertEquals(21,collectGet);
    assertEquals(21,regularGet);
    assertEquals(21,superget);

  }

  @Test
  public void testCounterDecement() {

    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("timestamp", 0);
    payload.put("category", "testing");
    payload.put("counters", new LinkedHashMap<String, Object>() {
      {
        put("decrement", 2);
      }
    });

    JsonNode node = resource().path("/test-organization/test-app/events")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

    assertNotNull(node.get("entities"));

    JsonNode rest = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","decrement")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals("decrement",rest.get("counters").get(0).get("name").getTextValue());
    assertEquals(2,rest.get("counters").get(0).get("values").get(0).get("value").getIntValue());
    //JsonNode rest = null;

    for(int i = 0; i < 1000; i++) {
      node = resource().path("/test-organization/test-app/events")
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .post(JsonNode.class, payload);
    }

    rest = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","decrement")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals(2002,rest.get("counters").get(0).get("values").get(0).get("value").getIntValue());

    payload = new LinkedHashMap<String, Object>();
    payload.put("timestamp", 0);
    payload.put("category", "testing");
    payload.put("counters", new LinkedHashMap<String, Object>() {
      {
        put("decrement", -2);
      }
    });

    for(int i = 0; i < 1000; i++) {
      node = resource().path("/test-organization/test-app/events")
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .post(JsonNode.class, payload);
    }

    rest = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","decrement")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals(2,rest.get("counters").get(0).get("values").get(0).get("value").getIntValue());

    for(int i = 0; i < 1000; i++) {
      node = resource().path("/test-organization/test-app/events")
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .post(JsonNode.class, payload);
    }

    rest = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","decrement")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals(-1998,rest.get("counters").get(0).get("values").get(0).get("value").getIntValue());

  }

  @Test
  public void testCounterReset() {

    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("timestamp", 0);
    payload.put("category", "testing");
    payload.put("counters", new LinkedHashMap<String, Object>() {
      {
        put("reset", 1);
      }
    });

    JsonNode node = resource().path("/test-organization/test-app/events")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

    assertNotNull(node.get("entities"));

    node = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","reset")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    payload = new LinkedHashMap<String, Object>();
    payload.put("timestamp", 0);
    payload.put("category", "testing");
    payload.put("counters", new LinkedHashMap<String, Object>() {
      {
        put("reset", 0);
      }
    });

    node = resource().path("/test-organization/test-app/events")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

    node = resource().path("/test-organization/test-app/counters")
        .queryParam("counter","reset")
        .queryParam("access_token", superAdminToken())
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);

    assertEquals(0,node.get("counters").get(0).get("values").get(0).get("value").getIntValue());

  }

}
