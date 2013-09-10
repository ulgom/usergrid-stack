package org.usergrid.mq.cassandra.io;

import org.junit.Assert;
import me.prettyprint.hector.api.Keyspace;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usergrid.CoreITSuite;
import org.usergrid.mq.QueueQuery;
import org.usergrid.persistence.cassandra.CassandraService;
import org.usergrid.mq.cassandra.io.NoTransactionSearch.SearchParam;
import org.usergrid.persistence.exceptions.QueueException;

import java.util.UUID;


public class EndSearchTest {
    private static final Logger logger = LoggerFactory.getLogger(EndSearchTest.class);


    private UUID applicationId = UUID.randomUUID();
    private CassandraService cass = CoreITSuite.cassandraResource.getBean( CassandraService.class );
    Keyspace ko = cass.getApplicationKeyspace(applicationId);

    @Test
    public void testGetResults() {
        UUID consumerId = UUID.randomUUID();
        EndSearch es = new EndSearch(ko);
        QueueQuery qq = new QueueQuery();
        UUID queueId = UUID.randomUUID();
        qq.setConsumerId(consumerId);
        qq.setLastMessageId(UUID.randomUUID());
        qq.withConsumer("consumer1");
        SearchParam sp = es.getParams(queueId,consumerId,qq);
        Assert.assertEquals(true,sp.reversed);
        es.writeClientPointer(queueId,consumerId,UUID.randomUUID());
    }

    @Test
    public void testGetQueueRangeBoundsNull() {
        UUID consumerId = UUID.randomUUID();
        EndSearch es = new EndSearch(ko);
        QueueQuery qq = new QueueQuery();
        UUID queueId = UUID.randomUUID();
        qq.setConsumerId(consumerId);
        qq.setLastMessageId(UUID.randomUUID());
        qq.withConsumer("consumer1");
        SearchParam sp = es.getParams(queueId,consumerId,qq);
        try{
        es.getQueueRange(queueId,null,sp);
        }catch(QueueException qe){
          Assert.assertEquals("class org.usergrid.persistence.exceptions.QueueException", qe.getClass().toString());
        }
    }

    @Test
    public void testGetQueueRange() {
        UUID consumerId = UUID.randomUUID();
        EndSearch es = new EndSearch(ko);
        QueueBounds qb = new QueueBounds(null,null);

        QueueQuery qq = new QueueQuery();
        UUID queueId = UUID.randomUUID();
        qq.setConsumerId(consumerId);
        qq.setLastMessageId(UUID.randomUUID());
        qq.withConsumer("consumer1");
        SearchParam sp = new SearchParam(null, true, true, 0);

        try{
            es.getQueueRange(queueId,qb,sp);
        }catch(QueueException qe){
            Assert.assertEquals("class org.usergrid.persistence.exceptions.QueueException", qe.getClass().toString());
        }
    }
}
