package org.usergrid.clustering.hazelcast;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.Instance;
import com.hazelcast.core.InstanceEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.Cluster;
import org.junit.Assert;
import org.junit.Test;
import org.usergrid.CoreITSuite;
import org.usergrid.persistence.cassandra.CassandraService;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: ApigeeCorporation
 * Date: 8/20/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class HazelcastLifecycleMonitorTest {

    @Test
    public void doHcLifecycleMonitor(){

        HazelcastLifecycleMonitor hm = new HazelcastLifecycleMonitor();
        hm.init();

        Collection<Instance> instances = Hazelcast.getInstances();

        for (Instance instance : instances) {
            InstanceEvent event = new InstanceEvent(InstanceEvent.InstanceEventType.CREATED,instance);

            Assert.assertEquals("",event.getInstanceType().name());
            hm.instanceCreated(event);
            Assert.assertEquals("created",event.getEventType().name());
            hm.instanceDestroyed(event);

            //hm.memberAdded(instanceDestroyed(event));
            //hm.memberRemoved(instanceDestroyed(event));
        }

       hm.destroy();
    }
}
