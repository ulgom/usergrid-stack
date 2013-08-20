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
package com.usergrid.count;

import com.usergrid.count.common.Count;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.factory.HFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static junit.framework.Assert.assertEquals;

public class CassandraSubmitterTest {

    protected static Logger logger = LoggerFactory
            .getLogger(CassandraSubmitterTest.class);
    private static Cluster cluster;

    @Before
    public void setupLocal() {
        cluster = HFactory.getOrCreateCluster("CounterTestCluster", new CassandraHostConfigurator("localhost:9170"));
    }
    @Test
    public void testCassandraSubmitter() {
        CassandraCounterStore cassandraCounterStore =
                new CassandraCounterStore(HFactory.createKeyspace("Keyspace1", cluster));
        CassandraSubmitter cassandraSubmitter = new CassandraSubmitter(cassandraCounterStore);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Collection<Count> counts = new ArrayList<Count>();
        Count count = new Count("Counters",1,"c1",1);
        counts.add(count);
        assertEquals(false, cassandraSubmitter.submit(counts).isDone());
    }

    @Test
    public void testShutdownCassandraSubmitter() {
        CassandraCounterStore cassandraCounterStore =
                new CassandraCounterStore(HFactory.createKeyspace("Keyspace1", cluster));
        CassandraSubmitter cassandraSubmitter = new CassandraSubmitter(cassandraCounterStore);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        cassandraSubmitter.shutdown();
        assertEquals(false, executor.isShutdown());
    }
}
