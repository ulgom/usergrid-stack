package org.usergrid.mq.cassandra.io;

import org.junit.Test;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: ApigeeCorporation
 * Date: 8/21/13
 * Time: 10:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class QueueBoundsTest {

    @Test
    public void testSearchQueueRange() {
        UUID oldId = UUID.randomUUID();
        UUID newId = UUID.randomUUID();

        QueueBounds qb = new QueueBounds(oldId,newId);
        QueueBounds qb1 = new QueueBounds(oldId,newId);
        qb.hashCode();
        qb.equals(qb)     ;
        qb.toString();
        qb.equals(null)   ;
        qb.equals(qb1);
    }
}
