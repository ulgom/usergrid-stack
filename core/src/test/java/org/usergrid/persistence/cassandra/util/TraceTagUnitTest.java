package org.usergrid.persistence.cassandra.util;

import org.junit.Before;
import org.junit.Test;
import org.usergrid.utils.UUIDUtils;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author zznate
 */
public class TraceTagUnitTest {

    private TraceTagManager traceTagManager;
    private Slf4jTraceTagReporter traceTagReporter;
    private TaggedOpTimer taggedOpTimer;

    @Before
    public void setup() {
        traceTagManager = new TraceTagManager();
        traceTagReporter = new Slf4jTraceTagReporter();
        taggedOpTimer = new TaggedOpTimer(traceTagManager);
    }

    @Test
    public void createAttachDetach() throws Exception {
        TraceTag traceTag = traceTagManager.create("testtag1");
        traceTagManager.attach(traceTag);
        TimedOpTag timedOpTag = (TimedOpTag)taggedOpTimer.start();
        Thread.currentThread().sleep(500);
        taggedOpTimer.stop(timedOpTag,"op-tag-name",true);
        assertTrue(timedOpTag.getElapsed() >= 500);
        assertEquals(timedOpTag, traceTag.iterator().next());
        traceTagManager.detach();
    }

    @Test
    public void testTraceTag() throws Exception {
        Slf4jTraceTagReporter straceTagReporter = new Slf4jTraceTagReporter();
        TraceTag traceTag = traceTagManager.create("testtag1");
        UUID id = UUIDUtils.newTimeUUID();
        traceTag.getMeteredInstance(id, "name");
        traceTag.getMetered();
        traceTag.removeOps();
        traceTag.toString();
        TimedOpTag timedOpTag = (TimedOpTag)taggedOpTimer.start();
        timedOpTag.getTraceTagName();
        timedOpTag.getOpTag();
        timedOpTag.getTraceTagName();
        timedOpTag.getOpSuccessful();
        timedOpTag.getTagName();
        straceTagReporter.report(traceTag);
        straceTagReporter.reportUnattached(timedOpTag);

    }

    @Test
    public void testTraceTagManager() throws Exception {
        TraceTagManager ttManager = new TraceTagManager();
        ttManager.toString();
        TraceTag traceTag =  ttManager.acquire();
        ttManager.setTraceEnabled(true);
        boolean te = ttManager.getTraceEnabled();
        int fc = ttManager.getFlushAtOpCount();
        ttManager.setReportUnattached(true);
        ttManager.setExplicitOnly(true);
        boolean eo = ttManager.getExplicitOnly();
        ttManager.createMetered("tag");
        ttManager.setFlushAtOpCount(2);



    }
}
