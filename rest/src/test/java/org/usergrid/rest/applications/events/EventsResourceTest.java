package org.usergrid.rest.applications.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.UniformInterfaceException;
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

  @Test //USERGRID-1742
  public void testEventPostandGetUUID() {

    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("category", "testcat");
    payload.put("timestamp","201111211437");

    JsonNode node = resource().path("/test-organization/test-app/events")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

    String uuid = node.get("entities").get(0).get("uuid").getTextValue();

    try{
    node = resource().path("/test-organization/test-app/events/"+uuid)
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);
    }catch(UniformInterfaceException uie){
      assertEquals(200,uie.getResponse().getStatus());
      return;
    }

  }

  @Test
  public void putToUpdateEvents() {

    Map<String, Object> payload = new LinkedHashMap<String, Object>();
    payload.put("category", "testcat");
    payload.put("timestamp","201111211437");

    JsonNode node = resource().path("/test-organization/test-app/events")
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .post(JsonNode.class, payload);

    String uuid = node.get("entities").get(0).get("uuid").getTextValue();
    payload.put("category", "testbat");

    try{
      node = resource().path("/test-organization/test-app/events/"+uuid)
          .queryParam("access_token", access_token)
          .accept(MediaType.APPLICATION_JSON)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .put(JsonNode.class,payload);
    }catch(UniformInterfaceException uie){
      assertEquals(200,uie.getResponse().getStatus());
      return;
    }

    try {
    node = resource().path("/test-organization/test-app/events/"+uuid)
        .queryParam("access_token", access_token)
        .accept(MediaType.APPLICATION_JSON)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .get(JsonNode.class);
  }catch(UniformInterfaceException uie){
    assertEquals(200,uie.getResponse().getStatus());
    return;
  }
    //need to add certain line that would verify that the testcat was updated to testbat. Not
    //sure what the response for events would look like so I'm leaving it blank for now.

  }

}