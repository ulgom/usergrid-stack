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
package org.usergrid.locking.exception;

import org.junit.Assert;
import org.junit.Test;


public class UGLockExceptionTest {

    @Test
    public void testConstructor() throws Exception{
        UGLockException ugle = new UGLockException();
        Assert.assertEquals("org.usergrid.locking.exception.UGLockException", ugle.toString());

    }

    @Test
    public void testConstructorWithMessage() throws Exception{
        UGLockException ex = new UGLockException("Test");

        Assert.assertEquals("org.usergrid.locking.exception.UGLockException: Test", ex.toString());
    }

    @Test
    public void testConstructorWithMessageThrowable() throws Exception{
        Throwable ex = new Throwable("error");
        UGLockException cex = new UGLockException("Test", ex);
        Assert.assertEquals("org.usergrid.locking.exception.UGLockException: Test", cex.toString());
    }

    @Test
    public void testConstructorWithThrowable() throws Exception{
        Throwable ex = new Throwable("error");
        UGLockException cex = new UGLockException(ex);
        Assert.assertEquals("org.usergrid.locking.exception.UGLockException: java.lang.Throwable: error", cex.toString());
    }
}
