package org.usergrid.mq.cassandra.io;

import me.prettyprint.hector.api.Keyspace;
import org.junit.Assert;
import org.junit.Test;
import org.usergrid.CoreITSuite;
import org.usergrid.mq.QueueQuery;
import org.usergrid.persistence.cassandra.CassandraService;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ApigeeCorporation
 * Date: 8/21/13
 * Time: 10:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class StartSearchTest {
    private UUID applicationId = UUID.randomUUID();
    private CassandraService cass = CoreITSuite.cassandraResource.getBean( CassandraService.class );
    Keyspace ko = cass.getApplicationKeyspace(applicationId);

    @Test
    public void testGetResults() {
        UUID consumerId = UUID.randomUUID();
        StartSearch es = new StartSearch(ko);
        QueueQuery qq = new QueueQuery();
        UUID queueId = UUID.randomUUID();
        qq.setConsumerId(consumerId);
        qq.setLastMessageId(UUID.randomUUID());
        qq.withConsumer("consumer1");
        NoTransactionSearch.SearchParam sp = es.getParams(queueId,consumerId,qq);
        Assert.assertEquals(false, sp.reversed);
        es.writeClientPointer(queueId,consumerId,UUID.randomUUID());
    }
}
