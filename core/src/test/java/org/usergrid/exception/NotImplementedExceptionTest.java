package org.usergrid.exception;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.usergrid.Application;
import org.usergrid.SimpleApplication;


public class NotImplementedExceptionTest {



    @Test
    public void testConstructor() throws Exception{
        NotImplementedException ex = new NotImplementedException();
        Assert.assertEquals("org.usergrid.exception.NotImplementedException", ex.toString());

    }

    @Test
    public void testConstructorWithMessage() throws Exception{
        NotImplementedException ex = new NotImplementedException("Test");

        Assert.assertEquals("org.usergrid.exception.NotImplementedException: Test", ex.toString());
    }

    @Test
    public void testConstructorWithMessageThrowable() throws Exception{
        Throwable ex = new Throwable("error");
        NotImplementedException cex = new NotImplementedException("Test", ex);
        Assert.assertEquals("org.usergrid.exception.NotImplementedException: Test", cex.toString());
    }

    @Test
    public void testConstructorWithThrowable() throws Exception{
        Throwable ex = new Throwable("error");
        NotImplementedException cex = new NotImplementedException(ex);
        Assert.assertEquals("org.usergrid.exception.NotImplementedException: java.lang.Throwable: error", cex.toString());
    }
}
