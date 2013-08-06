package org.usergrid.rest.applications.collection.groups;

import org.codehaus.jackson.JsonNode;
import org.junit.Ignore;
import org.junit.Test;
import org.usergrid.rest.RestContextTest;
import org.usergrid.rest.test.resource.CustomCollection;

import javax.ws.rs.core.MediaType;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.usergrid.utils.MapUtils.hashMap;

/**
 * // TODO: Document this
 *
 * @author ApigeeCorporation
 * @since 4.0
 */
public class CustomCollectionTest  extends RestContextTest {

  @Test //Check to make sure that asc works
  public void queryCheckAsc() throws Exception{

    CustomCollection madeupStuff = collection("imagination");
    Map character = hashMap("WhoHelpedYou","Ruff");

    JsonNode[] correctValues = new JsonNode[1000];


    correctValues = madeupStuff.createEntitiesWithOrdinal(character,1000);

    String inquisitiveQuery = "select * where Ordinal gte 0 and Ordinal lte 2000 or WhoHelpedYou eq 'Ruff' ORDER BY " +
        "Ordinal asc";

    int totalEntitiesContained = madeupStuff.verificationOfQueryResults(correctValues,false,inquisitiveQuery);

    assertEquals(1000,totalEntitiesContained);
  }

  @Ignore//Test to make sure all 1000 exist with a regular query
  public void queryReturnCheck() throws Exception{
    CustomCollection madeupStuff = collection("imagination");
    Map character = hashMap("WhoHelpedYou","Ruff");

    int numOfEntities = 1000;

    JsonNode[] correctValues = madeupStuff.createEntitiesWithOrdinal(character,numOfEntities);

    String inquisitiveQuery = "select * where Ordinal >= 0 and Ordinal <= 2000 or WhoHelpedYou = 'Ruff'";

    int totalEntitiesContained = madeupStuff.verificationOfQueryResults(correctValues,true,inquisitiveQuery);

    assertEquals(numOfEntities,totalEntitiesContained);
  }

  @Ignore
  public void queryReturnCheckWithShortHand() {
    CustomCollection madeupStuff = collection("imagination");
    Map character = hashMap("WhoHelpedYou","Ruff");

    madeupStuff.createEntitiesWithOrdinal(character,1000);

    String inquisitiveQuery = "select * where Ordinal gte 0 and Ordinal lte 2000 or WhoHelpedYou eq 'Ruff'";

    int totalEntitiesContained = madeupStuff.countEntities(inquisitiveQuery);

    assertEquals(1000,totalEntitiesContained);

  }

  @Test //USERGRID-1020
  public void entityDeletion() {

    CustomCollection music = collection("sandbox");
    Map musician = hashMap("band",1);
    Map newMusician = hashMap("band",2);

    music.create(musician);
    music.create(newMusician);

    String query = "created > 1";

    JsonNode node = music.withQuery(query).get();

    assertNotNull(node.get("entities").get(0));
    assertNotNull(node.get("entities").get(1));

    music.withQuery(query).delete(hashMap("band",1));

    node = music.withQuery(query).get();
    assertNull(node.get("entities").get(0));

  }
}
