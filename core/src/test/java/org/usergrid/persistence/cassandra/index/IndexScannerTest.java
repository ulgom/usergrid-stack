package org.usergrid.persistence.cassandra.index;

import org.junit.Test;
import org.usergrid.CoreITSuite;
import org.usergrid.persistence.EntityRef;
import org.usergrid.persistence.IndexBucketLocator;
import org.usergrid.persistence.cassandra.ApplicationCF;
import org.usergrid.persistence.cassandra.CassandraService;
import org.usergrid.persistence.cassandra.ConnectedEntityRefImpl;
import org.usergrid.persistence.cassandra.ConnectionRefImpl;
import org.usergrid.persistence.cassandra.index.ConnectedIndexScanner;
import org.usergrid.persistence.cassandra.index.IndexBucketScanner;
import org.usergrid.persistence.cassandra.index.NoOpIndexScanner;
import org.usergrid.persistence.cassandra.index.IndexMultiBucketSetLoader;
import org.usergrid.utils.UUIDUtils;
import org.usergrid.persistence.ConnectedEntityRef;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.lang.StringUtils.split;
import static org.usergrid.persistence.Schema.getDefaultSchema;
import static org.usergrid.persistence.cassandra.ApplicationCF.ENTITY_COMPOSITE_DICTIONARIES;
import static org.usergrid.persistence.cassandra.ApplicationCF.ENTITY_DICTIONARIES;
import static org.usergrid.persistence.cassandra.ApplicationCF.ENTITY_ID_SETS;

/**
 * Created with IntelliJ IDEA.
 * User: ApigeeCorporation
 * Date: 9/5/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class IndexScannerTest {
    @Test
    public void testConnectedIndexScanner() {
        CassandraService cassandraService = CoreITSuite.cassandraResource.getBean( CassandraService.class );
        UUID appId = UUIDUtils.newTimeUUID();
        ConnectedEntityRefImpl connectedEntity = new ConnectedEntityRefImpl();
        String[] parts = {"", "", ""};
        ByteBuffer bb = ByteBuffer
                .wrap(decodeBase64(parts[1]));
        ConnectedIndexScanner cis = new ConnectedIndexScanner(cassandraService, "", appId,
                ConnectionRefImpl.toConnectedEntity(connectedEntity),bb, true, 3);
        cis.reset();
        cis.hasNext();
        cis.getPageSize();
        cis.next();

        cis.iterator();
        try{
            boolean l = cis.load();
            cis.remove();
        }catch(Exception e){

        }

    }

    @Test
    public void testIndexBucketScanner() {
        CassandraService cassandraService = CoreITSuite.cassandraResource.getBean( CassandraService.class );
        UUID appId = UUIDUtils.newTimeUUID();
        IndexBucketLocator locator = CoreITSuite.cassandraResource.getBean( IndexBucketLocator.class );
        IndexBucketScanner ibs = new IndexBucketScanner(cassandraService,locator ,
                ENTITY_ID_SETS, appId, IndexBucketLocator.IndexType.COLLECTION, "",
                "", "", true, 3, "");
        ibs.iterator();
        try{
            ibs.remove();
        }  catch(UnsupportedOperationException e)    {


        }

    }


    @Test
    public void testIndexMultiBucketSetLoader() {
        CassandraService cassandraService = CoreITSuite.cassandraResource.getBean( CassandraService.class );
        ConnectedEntityRefImpl entity = new ConnectedEntityRefImpl();
        ApplicationCF cf = getDefaultSchema().hasDictionary(entity.getType(),
                "") ? ENTITY_DICTIONARIES
                : ENTITY_COMPOSITE_DICTIONARIES;
        UUID appId = UUIDUtils.newTimeUUID();
        List<Object> list = new ArrayList<Object>();
        list.add("");
        IndexMultiBucketSetLoader imb = new IndexMultiBucketSetLoader();
        try{
            imb.load(cassandraService, cf, appId, list, "", "",3, true);
        }catch(Exception e)   {

        }


    }

    @Test
    public void testNoOpIndexScanner() {
        NoOpIndexScanner nis = new NoOpIndexScanner();
        nis.iterator();
        nis.next();
        nis.getPageSize();
        nis.hasNext();
        nis.reset();
        try{
            nis.remove();
        }  catch(UnsupportedOperationException e)    {


        }

    }
}
