package org.usergrid;


import org.usergrid.batch.job.CountdownLatchJob;
import org.usergrid.batch.service.JobSchedulerService;
import org.usergrid.batch.service.SchedulerService;

import java.util.Properties;


/**
 * A TestRule that is used to setup various resources for the scheduler modules tests.
 */
public interface SchedulerITSetup extends CoreITSetup
{
    Properties getProps();

    SchedulerService getSs();

    JobSchedulerService getJss();

    CountdownLatchJob getLatchJob();
}
