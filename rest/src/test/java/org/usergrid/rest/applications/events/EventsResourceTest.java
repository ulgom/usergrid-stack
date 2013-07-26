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

  }


}
