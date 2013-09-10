package org.usergrid.rest.test.security;

import com.sun.jersey.api.representation.Form;
import org.codehaus.jackson.JsonNode;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usergrid.management.ApplicationInfo;
import org.usergrid.management.OrganizationInfo;
import org.usergrid.rest.AbstractRestIT;
import org.usergrid.rest.ITSetup;
import org.usergrid.rest.RestITSuite;
import org.usergrid.management.UserInfo;
import org.usergrid.rest.management.organizations.OrganizationsResource;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static org.usergrid.utils.MapUtils.hashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ApigeeCorporation
 * Date: 8/29/13
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestUserEmailCase extends AbstractRestIT {

    private static final Logger logger = LoggerFactory.getLogger(TestUserEmailCase.class);

    //@ClassRule
   // public static ITSetup setup = new ITSetup( RestITSuite.cassandraResource );

   /* @Test
    public void createUserEmailUpper() {
        try{
        UserInfo user = setup.getMgmtSvc().createAdminUser("rippela1", "renu", "Rippela@apigee.com", "password1",
                true, false);

            Assert.assertEquals(null,user.getUuid());
        }
        catch(Exception e){
            logger.info("Exception in createUserEmailUpper ");
        }


    }

    @Test
    public void createUserEmailLower() {
        try{
            UserInfo user = setup.getMgmtSvc().createAdminUser("rippela", "renu", "rippela@apigee.com", "password1",
                    true, false);

            Assert.assertEquals(null,user.getUuid());
        }
        catch(Exception e){
            logger.info("Exception in createUserEmailLower ");
        }


    }

    @Test
    public void createUserEmail() {
        try{
            UserInfo user = setup.getMgmtSvc().createAdminUser("rsimpson+instaops@apigee.com", "Insta ops", "rsimpson+instaops@apigee.com", "test1test",
                    true, false);
            OrganizationInfo orgInfo = setup.getMgmtSvc().getOrganizationByName("instaops2-prod");
            setup.getMgmtSvc().addAdminUserToOrganization(user,orgInfo, true);
            Assert.assertEquals(null,orgInfo.getUuid());
        }
        catch(Exception e){
            logger.info("Exception in createUserEmail ");
        }


    }

    @Test
    public void createOrgInfo() {
        try{
            UserInfo user = setup.getMgmtSvc().createAdminUser("rsimpson+instaops@apigee.com", "Insta ops", "rsimpson+instaops@apigee.com", "test1test",
                    true, false);
            logger.info("User created");
            OrganizationInfo orgInfo = setup.getMgmtSvc().createOrganization("instaops2-prod", user, true);
            //setup.getMgmtSvc().addAdminUserToOrganization(user,orgInfo, true);
            logger.info("Org created");
            Assert.assertEquals(null,orgInfo.getUuid());
        }
        catch(Exception e){
            logger.info("Exception in createOrgInfo ");
        }


    }


    @Test
    public void createOrgInfoa() {
    Map<String, Object> organizationProperties = new HashMap<String,Object>();
    organizationProperties.put("securityLevel", 5);

    Map payload = hashMap("email",
            "rsimpson+instaops@apigee.com").map("username", "rsimpson+instaops@apigee.com")
            .map("name", "Insta ops").map("password", "test1test")
            .map("organization", "instaops2-prod")
            .map("company","Apigee");
    payload.put(OrganizationsResource.ORGANIZATION_PROPERTIES, organizationProperties);

    JsonNode node = resource().path("/management/organizations")
            .accept(MediaType.APPLICATION_JSON)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .post(JsonNode.class, payload);

    assertNotNull(node);


    }  */


    @Test
    public void testOrgPOSTParams() {
        JsonNode node = resource().path("/management/organizations")
                .queryParam("organization", "instaops2-prod")
                .queryParam("username", "rsimpson+instaops@apigee.com")
                .queryParam("grant_type", "password")
                .queryParam("email", "rsimpson+instaops@apigee.com")
                .queryParam("name", "Insta ops")
                .queryParam("password", "test1test")

                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_FORM_URLENCODED)
                .post(JsonNode.class);

        assertEquals("ok", node.get("status").asText());

    }

    @Test
    public void testOrgPOSTForm() {

        Form form = new Form();
        form.add("organization", "instaops2-prod");
        form.add("username", "rsimpson+instaops@apigee.com");
        form.add("grant_type", "password");
        form.add("email", "rsimpson+instaops@apigee.com");
        form.add("name", "Insta ops");
        form.add("password", "test1test");

        JsonNode node = resource().path("/management/organizations")
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_FORM_URLENCODED)
                .post(JsonNode.class, form);

        assertEquals("ok", node.get("status").asText());

    }
}
