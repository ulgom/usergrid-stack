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

import org.junit.Assert;
import org.junit.Test;


public class CounterProcessingUnavailableExceptionTest {


    @Test
    public void testConstructor() throws Exception{
        CounterProcessingUnavailableException ex = new CounterProcessingUnavailableException();

        Assert.assertEquals("com.usergrid.count.CounterProcessingUnavailableException: Counter was not processed. Reason: ", ex.toString());
    }

    @Test
    public void testConstructorWithMessage() throws Exception{
        CounterProcessingUnavailableException ex = new CounterProcessingUnavailableException("Test");

        Assert.assertEquals("com.usergrid.count.CounterProcessingUnavailableException: Counter was not processed. Reason: Test", ex.toString());
    }

    @Test
    public void testConstructorWithMessageThrowable() throws Exception{
        Throwable ex = new Throwable("error");
        CounterProcessingUnavailableException cex = new CounterProcessingUnavailableException("Test", ex);
        Assert.assertEquals("com.usergrid.count.CounterProcessingUnavailableException: Counter was not processed. Reason: Test", cex.toString());
    }

}
