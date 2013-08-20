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
package com.usergrid.count.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CountTransportSerDeExceptionTest {

    @Test
    public void testConstructor() throws Exception{
        CountTransportSerDeException ex = new CountTransportSerDeException();

        assertEquals("com.usergrid.count.common.CountTransportSerDeException: There was a serialization/deserialization problem in Count transport. Reason: ", ex.toString());
    }

    @Test
    public void testConstructorWithMessage() throws Exception{
        CountTransportSerDeException ex = new CountTransportSerDeException("Test");

        assertEquals("com.usergrid.count.common.CountTransportSerDeException: There was a serialization/deserialization problem in Count transport. Reason: Test", ex.toString());
    }


}
