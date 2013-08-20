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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static junit.framework.Assert.assertEquals;


public class AbstractBatcherTest {

    protected static Logger logger = LoggerFactory
            .getLogger(AbstractBatcherTest.class);

    @Test
    public void testGetOpCount() {
        SimpleBatcher simpleBatcher = new SimpleBatcher();
        assertEquals(0, simpleBatcher.getOpCount());
    }

    @Test
    public void testAddBatchSerial() {
        SimpleBatcher simpleBatcher = new SimpleBatcher();
        Count count = new Count("Counters",1,"c1",1);
        try{
            simpleBatcher.getBatch().addSerial(count);
        }
        catch(Exception ex){
            logger.error( "Error while testing SimpleBatcherTest:addSerial", ex );
        }
    }


    @Test
    public void testAddCountBatchSizeOne() {
        SimpleBatcher simpleBatcher = new SimpleBatcher();
        simpleBatcher.setBatchSize(1);
        Count count = new Count("Counters","k1","c1",1);
        try{
            simpleBatcher.add(count);
        }
        catch(Exception ex){
            logger.error( "Error while testing SimpleBatcherTest:testAddCountBatchSizeOne", ex );
        }
        assertEquals(1, simpleBatcher.getBatch().getCapacity());

    }

    @Test
    public void testAddCountException() {
        SimpleBatcher simpleBatcher = new SimpleBatcher();
        Count count = null;
        try{
            simpleBatcher.getBatch().add(count);
        }catch(Exception ex){
            logger.error("Error while testing SimpleBatcherTest:testAddCountException", ex);
        }
    }

    @Test
    public void testAddBatchSerialException() {
        SimpleBatcher simpleBatcher = new SimpleBatcher();
        Count count = null;
        try{
            simpleBatcher.getBatch().addSerial(count);
        }catch(Exception ex){
            logger.error("Error while testing SimpleBatcherTest:testAddCountException", ex);
        }
    }

    @Test
    public void testGetLocalCallCount() {
        SimpleBatcher simpleBatcher = new SimpleBatcher();
        assertEquals(0, simpleBatcher.getBatch().getLocalCallCount());
    }


}
