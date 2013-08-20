package org.usergrid.locking;


import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.usergrid.AbstractCoreIT;
import org.usergrid.CoreITSuite;
import org.usergrid.locking.cassandra.HectorLockManagerImpl;
import org.usergrid.locking.exception.UGLockException;
import org.usergrid.locking.noop.NoOpLockImpl;
import org.usergrid.persistence.cassandra.CassandraService;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.usergrid.locking.LockHelper.getUniqueUpdateLock;


public class LockHelperTest {

    private static LockManager lockManager;
    private static ExecutorService pool;

    @BeforeClass
    public static void setup() throws Exception
    {
        AbstractCoreIT.setup();

        HectorLockManagerImpl hlockManager = new HectorLockManagerImpl();
        hlockManager.setCluster(CoreITSuite.cassandraResource.getBean( CassandraService.class ).getCluster() );
        hlockManager.setKeyspaceName( "Locks" );
        hlockManager.setLockTtl( 2000 );
        hlockManager.setNumberOfLockObserverThreads( 1 );
        hlockManager.setReplicationFactor( 1 );
        hlockManager.init();

        lockManager = hlockManager;
    }


    @Before
    public void start()
    {
        // Create a different thread to lock the same node, that is held by the main thread.
        pool = Executors.newFixedThreadPool(1);
    }


    @After
    public void tearDown() throws Exception
    {
        pool.shutdownNow();
    }


    @Test
    public void testGetUniqueUpdateLockNull(){

        final UUID application = UUID.randomUUID();
        try{
            Lock lock = getUniqueUpdateLock(lockManager, application, null, "path");
            assertEquals(NoOpLockImpl.class,lock.getClass());
        }catch(UGLockException e){

        }


    }
    @Test
    public void testGetUniqueUpdateLock(){

        final UUID application = UUID.randomUUID();
        try{
            Lock lock = getUniqueUpdateLock(lockManager, application, "name", "path");
            assertEquals(true,lock.tryLock(3, TimeUnit.SECONDS));
        }catch(UGLockException e){

        }


    }
}
