package org.usergrid.mq.cassandra.io;

import me.prettyprint.hector.api.Keyspace;
import org.junit.Assert;
import org.junit.Test;
import org.usergrid.CoreITSuite;
import org.usergrid.exception.NotImplementedException;
import org.usergrid.mq.QueueQuery;
import org.usergrid.persistence.cassandra.CassandraService;
import org.usergrid.mq.QueueResults;

import java.util.UUID;



/**
 * Created with IntelliJ IDEA.
 * User: ApigeeCorporation
 * Date: 8/19/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilterSearchTest {

    private UUID applicationId = UUID.randomUUID();
    private UUID queueId = UUID.randomUUID();
    private CassandraService cass = CoreITSuite.cassandraResource.getBean( CassandraService.class );
    Keyspace ko = cass.getApplicationKeyspace(applicationId);
    @Test
    public void testGetResults() {
        String queuePath = "/a/b/c/";
        QueueQuery qq = new QueueQuery();
        UUID id = UUID.randomUUID();
        qq.setConsumerId(id);
        qq.setLastMessageId(id);
        qq.setReversed(true);
        qq.withConsumer("consumer1");

        FilterSearch fs = new FilterSearch(ko);
       // QueueResults qr = fs.getResults(queuePath, qq) ;
        //Assert.assertEquals("org.usergrid.exception.NotImplementedException: Test", ex.toString());
    }

    @Test
    public void testSearchQueueRange() {
        String queuePath = "/foo/bar";

        QueueQuery qq = new QueueQuery();
        UUID id = UUID.randomUUID();
        qq.setConsumerId(id);
        qq.setLastMessageId(id);

        qq.withConsumer("consumer1");

        FilterSearch fs = new FilterSearch(ko);

        //ReverseUUIDComparator innerObject = fs.new ReverseUUIDComparator();
       // fs.newcompare()
       QueueBounds bounds = fs.getQueueBounds(queueId);
       // Assert.assertEquals(1,bounds.hashCode());
        //QueueResults qr = fs.searchQueueRange(ko,queueId,bounds, queuePath, qq, false, ) ;
        //Assert.assertEquals("org.usergrid.exception.NotImplementedException: Test", ex.toString());
    }
}
