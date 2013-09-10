package org.usergrid.mq.cassandra.io;

import org.junit.Assert;
import me.prettyprint.hector.api.Keyspace;
import org.junit.Test;
import org.usergrid.CoreITSuite;
import org.usergrid.locking.LockManager;
import org.usergrid.locking.noop.NoOpLockManagerImpl;
import org.usergrid.mq.QueueQuery;
import org.usergrid.persistence.cassandra.CassandraService;
import org.usergrid.mq.cassandra.io.NoTransactionSearch;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ApigeeCorporation
 * Date: 8/21/13
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConsumerTransactionTest {
    private UUID applicationId = UUID.randomUUID();
    private CassandraService cass = CoreITSuite.cassandraResource.getBean( CassandraService.class );
    Keyspace ko = cass.getApplicationKeyspace(applicationId);
    String queuePath = "/a/b/c/";
    private static LockManager lockManager = new NoOpLockManagerImpl();
    @Test
    public void testRenewTransaction() {
        String queuePath = "/a/b/c/";
        QueueQuery qq = new QueueQuery();
        UUID transactionId = UUID.randomUUID();
        qq.setConsumerId(transactionId);
        qq.setLastMessageId(transactionId);
        qq.setReversed(true);
        qq.withConsumer("consumer1");
        qq.setLimit(20000);
        ConsumerTransaction ct = new ConsumerTransaction(applicationId, ko, lockManager, cass);
        try{
        ct.renewTransaction(queuePath,transactionId, qq) ;
        }catch(Exception e){

        }
    }

    @Test
    public void testRenewTransactionQueryNull() {
        String queuePath = "/a/b/c/";

        UUID transactionId = UUID.randomUUID();

        ConsumerTransaction ct = new ConsumerTransaction(applicationId, ko, lockManager, cass);
        try{
            ct.renewTransaction(queuePath,transactionId, null) ;
        }catch(Exception e){

        }
    }


    @Test
    public void testDeleteTransactionPointers() {
        String queuePath = "/a/b/c/";

        UUID transactionId = UUID.randomUUID();
        ConsumerTransaction ct = new ConsumerTransaction(applicationId, ko, lockManager, cass);
        //ConsumerTransaction.TransactionPointer innerObject = ct.new TransactionPointer();

        //Assert.assertEquals("TransactionPointer", ct.TransactionPointer.toString().getClass());
        try{
       //     ct.deleteTransactionPointers(innerObject, transactionId, null) ;
        }catch(Exception e){

        }
    }

    @Test
    public void testDeleteTransactionQueryNull() {
        String queuePath = "/a/b/c/";

        UUID transactionId = UUID.randomUUID();

        ConsumerTransaction ct = new ConsumerTransaction(applicationId, ko, lockManager, cass);
        //Assert.assertEquals("TransactionPointer", ct.TransactionPointer.toString().getClass());
        try{
            ct.deleteTransaction(queuePath, transactionId, null) ;
        }catch(Exception e){

        }
    }

    @Test
    public void testGetResults() {
        String queuePath = "/a/b/c/";
        QueueQuery qq = new QueueQuery();
        UUID transactionId = UUID.randomUUID();
        qq.setConsumerId(transactionId);
        qq.setLastMessageId(transactionId);
        qq.setReversed(true);
        qq.withConsumer("consumer1");
        qq.setLimit(20000);


        ConsumerTransaction ct = new ConsumerTransaction(applicationId, ko, lockManager, cass);
        //Assert.assertEquals("TransactionPointer", ct.TransactionPointer.toString().getClass());
        try{
            ct.getResults(queuePath, qq) ;
        }catch(Exception e){

        }
    }
}
