/*******************************************************************************
 * Copyright 2012 Apigee Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.usergrid.mq;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usergrid.AbstractCoreIT;
import org.usergrid.Application;
import org.usergrid.SimpleApplication;
import org.usergrid.cassandra.Concurrent;
import org.usergrid.persistence.EntityManager;
import org.usergrid.persistence.Entity;
import org.usergrid.persistence.Results;
//import org.usergrid.persistence.Query;
import org.usergrid.mq.Query;
import org.usergrid.utils.JsonUtils;

import static org.junit.Assert.*;

@Concurrent()
public class MessagesIT extends AbstractCoreIT
{
	private static final Logger LOG = LoggerFactory.getLogger( MessagesIT.class );

    @Rule
    public Application app = new SimpleApplication( setup );

	public MessagesIT() {
		super();
	}

    @Test
    public void testCollection() throws Exception
    {
        app.add( "username", "edanuff" );
        app.add( "email", "ed@anuff.com" );
        Entity user = app.create( "user" );
        assertNotNull( user );
        app.add( "actor", new LinkedHashMap<String, Object>()
        { {
                put( "displayName", "Ed Anuff" );
                put( "objectType", "person" );
            } });
        app.add( "verb", "tweet" );
        app.add( "content", "I ate a sammich" );
        app.add( "ordinal", 3 );
        Entity activity = app.create( "activity" );
        assertNotNull( activity );
        LOG.info( "" + activity.getClass() );
        LOG.info( JsonUtils.mapToFormattedJsonString( activity ) );
        activity = app.get( activity.getUuid() );
        LOG.info( "Activity class = {}", activity.getClass() );
        LOG.info( JsonUtils.mapToFormattedJsonString( activity ) );
        app.addToCollection( user, "activities", activity );
        // test queries on the collection
        app.add( "actor", new LinkedHashMap<String, Object>()
        { {
                put( "displayName", "Ed Anuff" );
                put( "objectType", "person" );
            } });
        app.add( "verb", "tweet2" );
        app.add( "content", "I ate a pickle" );
        app.add( "ordinal", 2 );
        Entity activity2 = app.create( "activity" );
        activity2 = app.get( activity2.getUuid() );
        app.addToCollection( user, "activities", activity2 );
        app.add( "actor", new LinkedHashMap<String, Object>()
        { {
                put( "displayName", "Ed Anuff" );
                put( "objectType", "person" );
            } });
        app.add( "verb", "tweet2" );
        app.add( "content", "I ate an apple" );
        app.add( "ordinal", 1 );
        Entity activity3 = app.create( "activity" );
        activity3 = app.get( activity3.getUuid() );
        app.addToCollection( user, "activities", activity3 );
        // empty query
        Query query = new Query();
        /* Results r = app.searchCollection( user, "activities", query );
        assertEquals( 3, r.size() ); // success
        // query verb
        query = new Query().addEqualityFilter( "verb", "tweet2" );
        r = app.searchCollection( user, "activities", query );
        assertEquals( 2, r.size() );
        // query verb, sort created
        query = new Query().addEqualityFilter( "verb", "tweet2" );
        query.addSort( "created" );
        r = app.searchCollection( user, "activities", query );
        assertEquals( 2, r.size() );
        List<Entity> entities = r.getEntities();
        assertEquals( entities.get( 0 ).getUuid(), activity2.getUuid() );
        assertEquals( entities.get( 1 ).getUuid(), activity3.getUuid() );
        // query verb, sort ordinal
        query = new Query().addEqualityFilter( "verb", "tweet2" );
        query.addSort( "ordinal" );
        r = app.searchCollection( user, "activities", query );
        assertEquals( 2, r.size() );
        entities = r.getEntities();
        assertEquals( entities.get( 0 ).getUuid(), activity3.getUuid() );
        assertEquals( entities.get( 1 ).getUuid(), activity2.getUuid() );
        // empty query, sort content
        query = new Query();
        query.addSort( "content" );
        r = app.searchCollection( user, "activities", query );
        assertEquals( 3, r.size() );
        entities = r.getEntities();
        LOG.info( JsonUtils.mapToFormattedJsonString( entities ) );
        assertEquals( entities.get( 0 ).getUuid(), activity2.getUuid() );
        assertEquals( entities.get( 1 ).getUuid(), activity.getUuid() );
        assertEquals( entities.get( 2 ).getUuid(), activity3.getUuid() );
        // empty query, sort verb
        query = new Query();
        query.addSort( "verb" );
        r = app.searchCollection( user, "activities", query );
        assertEquals( 3, r.size() );
        // empty query, sort ordinal
        query = new Query();
        query.addSort( "ordinal" );
        r = app.searchCollection( user, "activities", query );
        assertEquals( 3, r.size() );
        entities = r.getEntities();
        assertEquals( entities.get( 0 ).getUuid(), activity3.getUuid() );
        assertEquals( entities.get( 1 ).getUuid(), activity2.getUuid() );
        assertEquals( entities.get( 2 ).getUuid(), activity.getUuid() );
        // query ordinal
        query = new Query().addEqualityFilter( "ordinal", 2 );
        r = app.searchCollection( user, "activities", query );
        assertEquals( 1, r.size() );
        // query ordinal and sort ordinal
        query = new Query().addEqualityFilter( "ordinal", 2 );
        query.addSort( "ordinal" );
        r = app.searchCollection( user, "activities", query );
        assertEquals( 1, r.size() );  */
    }

	@Test
	public void testMessages() throws Exception {
		LOG.info("MessagesIT.testMessages");

		UUID applicationId = setup.createApplication("testOrganization","testMessages");

		assertNotNull(applicationId);

		EntityManager em = getEntityManagerFactory().getEntityManager(
				applicationId);
		assertNotNull(em);

		LOG.info("Creating message #1");

		Message message = new Message();
		message.setStringProperty("foo", "bar");
		LOG.info(JsonUtils.mapToFormattedJsonString(message));

		LOG.info("Posting message #1 to queue /foo/bar");

		QueueManager qm = getQueueManagerFactory()
				.getQueueManager(applicationId);
		qm.postToQueue("/foo/bar", message);

		LOG.info("Getting message #1");

		message = qm.getMessage(message.getUuid());
		LOG.info(JsonUtils.mapToFormattedJsonString(message));

		LOG.info("Getting message from /foo/bar, should be message #1");

		QueueResults messages = qm.getFromQueue("/foo/bar", null);
		LOG.info(JsonUtils.mapToFormattedJsonString(messages));
		assertEquals(1, messages.size());

		LOG.info("Getting message from /foo/bar, should empty");

		messages = qm.getFromQueue("/foo/bar", null);
		LOG.info(JsonUtils.mapToFormattedJsonString(messages));
		assertEquals(0, messages.size());

		message = new Message();
		message.setStringProperty("name", "alpha");
		qm.postToQueue("/foo/bar", message);

		message = new Message();
		message.setStringProperty("name", "bravo");
		qm.postToQueue("/foo/bar", message);
/*
		messages = qm.getFromQueue("/foo/bar", null);
		LOG.info(JsonUtils.mapToFormattedJsonString(messages));
		assertEquals(1, messages.size());

		messages = qm.getFromQueue("/foo/bar", null);
		LOG.info(JsonUtils.mapToFormattedJsonString(messages));
		assertEquals(1, messages.size());

		messages = qm.getFromQueue("/foo/bar", null);
		LOG.info(JsonUtils.mapToFormattedJsonString(messages));
		assertEquals(0, messages.size());

		messages = qm.getFromQueue("/foo/bar",
				new QueueQuery().withPosition(QueuePosition.END)
						.withPreviousCount(3));
		LOG.info(JsonUtils.mapToFormattedJsonString(messages));
		assertEquals(3, messages.size());
*/
    TimeUnit.SECONDS.sleep(2);
		Map<String, Long> counters = qm.getQueueCounters("/");
    LOG.info("dumping counters...." + counters);
		LOG.info(JsonUtils.mapToFormattedJsonString(counters));
		assertEquals(1, counters.size());
		assertNotNull(counters.get("/foo/bar/"));
		assertEquals(new Long(3), counters.get("/foo/bar/"));
	}

	@Test
	public void testSubscriberSearch() throws Exception {

		UUID applicationId = setup.createApplication("testOrganization","testSubscriberSearch");
		assertNotNull(applicationId);

		EntityManager em = getEntityManagerFactory().getEntityManager(
				applicationId);
		assertNotNull(em);

		QueueManager qm = getQueueManagerFactory()
				.getQueueManager(applicationId);

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("foo", "alpha");
		Queue q = qm.updateQueue("/foo/1/", properties);
		LOG.info(JsonUtils.mapToFormattedJsonString(q));

		q = qm.getQueue("/foo/1/");
		LOG.info(JsonUtils.mapToFormattedJsonString(q));
		assertEquals("alpha", q.getStringProperty("foo"));

		properties = new HashMap<String, Object>();
		properties.put("foo", "bravo");
		q = qm.updateQueue("/foo/2/", properties);
		LOG.info(JsonUtils.mapToFormattedJsonString(q));

		properties = new HashMap<String, Object>();
		properties.put("foo", "charlie");
		q = qm.updateQueue("/foo/3/", properties);
		LOG.info(JsonUtils.mapToFormattedJsonString(q));

		qm.subscribeToQueue("/pubtest/", "/foo/1/");
		qm.subscribeToQueue("/pubtest/", "/foo/2/");
		qm.subscribeToQueue("/pubtest/", "/foo/3/");

		QueueSet results = qm.searchSubscribers("/pubtest/",
				Query.findForProperty("foo", "bravo"));
		LOG.info(JsonUtils.mapToFormattedJsonString(results));
		assertEquals(1, results.size());

		properties = new HashMap<String, Object>();
		properties.put("foo", "delta");
		q = qm.updateQueue("/foo/2/", properties);
		LOG.info(JsonUtils.mapToFormattedJsonString(q));

		results = qm.searchSubscribers("/pubtest/",
				Query.findForProperty("foo", "bravo"));
		LOG.info(JsonUtils.mapToFormattedJsonString(results));
		assertEquals(0, results.size());

		results = qm.searchSubscribers("/pubtest/",
				Query.findForProperty("foo", "delta"));
		LOG.info(JsonUtils.mapToFormattedJsonString(results));
		assertEquals(1, results.size());

		qm.unsubscribeFromQueue("/pubtest/", "/foo/2/");

		results = qm.searchSubscribers("/pubtest/",
				Query.findForProperty("foo", "delta"));
		LOG.info(JsonUtils.mapToFormattedJsonString(results));
		assertEquals(0, results.size());
	}

	@Test
	public void testConsumer() throws Exception {

		UUID applicationId = setup.createApplication("testOrganization","testConsumer");
		assertNotNull(applicationId);

		EntityManager em = getEntityManagerFactory().getEntityManager(
				applicationId);
		assertNotNull(em);

		LOG.info("Creating messages");

		QueueManager qm = getQueueManagerFactory()
				.getQueueManager(applicationId);
		Message message;

		for (int i = 0; i < 10; i++) {
			message = new Message();
			message.setStringProperty("foo", "bar" + i);

			LOG.info("Posting message #" + i + " to queue /foo/bar: "
                    + message.getUuid());

			qm.postToQueue("/foo/bar", message);
		}

		for (int i = 0; i < 11; i++) {
			QueueResults messages = qm.getFromQueue("/foo/bar",
					new QueueQuery().withConsumer("consumer1"));
			LOG.info(JsonUtils.mapToFormattedJsonString(messages));
			if (i < 10) {
				assertEquals(1, messages.size());
				assertEquals("bar" + i, messages.getMessages().get(0)
						.getStringProperty("foo"));
			} else {
				assertEquals(0, messages.size());
			}
		}

		for (int i = 0; i < 11; i++) {
			QueueResults messages = qm.getFromQueue("/foo/bar",
					new QueueQuery().withConsumer("consumer2"));
			LOG.info(JsonUtils.mapToFormattedJsonString(messages));
			if (i < 10) {
				assertEquals(1, messages.size());
				assertEquals("bar" + i, messages.getMessages().get(0)
						.getStringProperty("foo"));
			} else {
				assertEquals(0, messages.size());
			}
		}
	}


    @Test
    public void testTransactions() throws Exception {

        UUID applicationId = setup.createApplication("testOrganization", "testTransactions");
        assertNotNull(applicationId);


    LOG.info("Creating messages");
        EntityManager em = getEntityManagerFactory().getEntityManager(applicationId);
        assertNotNull(em);

        LOG.info("Creating messages");

        QueueManager qm = getQueueManagerFactory().getQueueManager(applicationId);

        String queuePath = "/foo/bar";

        Message message = new Message();
        message.setStringProperty("foo", "bar");

        LOG.info("Posting message # to queue /foo/bar: " + message.getUuid());

        assertFalse(qm.hasMessagesInQueue(queuePath, null));

        qm.postToQueue(queuePath, message);
        assertTrue(qm.hasMessagesInQueue(queuePath, null));

        QueueQuery qq = new QueueQuery();
        qq.setTimeout(100);
        qq.setLimit(1);
        QueueResults qr = qm.getFromQueue(queuePath, qq);

        assertFalse(qm.hasMessagesInQueue(queuePath, null));
        assertTrue(qm.hasOutstandingTransactions(queuePath, null));
        assertTrue(qm.hasPendingReads(queuePath, null));

        qm.deleteTransaction(queuePath, qr.getMessages().get(0).getTransaction(), qq);

        assertFalse(qm.hasMessagesInQueue(queuePath, null));
        assertFalse(qm.hasOutstandingTransactions(queuePath, null));
        assertFalse(qm.hasPendingReads(queuePath, null));
    }

}
