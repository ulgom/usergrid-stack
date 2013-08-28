package org.usergrid;


import com.google.common.util.concurrent.Service;
import org.junit.runner.Description;
import org.usergrid.batch.job.CountdownLatchJob;
import org.usergrid.batch.service.JobSchedulerService;
import org.usergrid.batch.service.SchedulerService;
import org.usergrid.cassandra.CassandraResource;

import java.util.Properties;


public class SchedulerITSetupImpl extends CoreITSetupImpl implements SchedulerITSetup
{
    private SchedulerService ss;
    private JobSchedulerService jss;
    private Properties props;
    private CountdownLatchJob latchJob;


    public SchedulerITSetupImpl( CassandraResource cassandraResource )
    {
        super( cassandraResource );
    }


    @Override
    public Properties getProps()
    {
        return props;
    }


    @Override
    public SchedulerService getSs()
    {
        return ss;
    }

    @Override
    public JobSchedulerService getJss()
    {
        return jss;
    }


    @Override
    public CountdownLatchJob getLatchJob()
    {
        return latchJob;
    }


    @Override
    protected void before( Description description ) throws Throwable
    {
        super.before( description );

        ss = cassandraResource.getBean( SchedulerService.class );
        jss = cassandraResource.getBean( JobSchedulerService.class );
        props = cassandraResource.getBean( "properties", Properties.class );
        latchJob = cassandraResource.getBean( CountdownLatchJob.class );

        // start the scheduler after we're all set up
        if ( jss.state() != Service.State.RUNNING )
        {
            jss.startAndWait();
        }
    }
}
