import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * Created by rukman on 8/17/2017.
 */
public class ConnectionCheckerTest
{
    private Service service;
    private ServiceAccessHandler accessTimes;

    @Before public void setUp() throws Exception
    {
        service = getService();
        accessTimes = getHandler();
    }

    /* todo
       in this method  checker.checkServiceStatus();  checks the status of service
       by creating real time connection to the service. Which is not the proper way to use in test case.
       It needs to "Mock" the connecting method checkConnection() and run the test cse */

    @Test public void testCheckServiceStatus() throws Exception
    {

        // initial run of the thread
        updateLastAccessTime( 2000 );
        ConnectionChecker checker = new ConnectionChecker( service, accessTimes );
        checker.setStartTime( System.currentTimeMillis() );
        checker.setLastRunTime( System.currentTimeMillis() );
        checker.checkServiceStatus();
        assertTrue( "Initial checker service start ", ( service.getStatus() == Service.SERVICE_DOWN || service.getStatus() == Service.SERVICE_UP ) );

        // Iteration  that less than both polling frequency  and grace period
        updateLastAccessTime( 2000 );
        service.setStatus( Service.SERVICE_UNKNOWN );
        service.setLastUpdateTime( System.currentTimeMillis() - 5000 );
        checker.setStartTime( System.currentTimeMillis() - 3000 );
        checker.setLastRunTime( System.currentTimeMillis() - 1000 );
        checker.checkServiceStatus();
        assertTrue( "Status not going to change ", service.getStatus() == Service.SERVICE_UNKNOWN );

        // Iteration  that greater than polling frequency. but less than grace period
        updateLastAccessTime( 2000 );
        service.setStatus( Service.SERVICE_UNKNOWN );
        service.setLastUpdateTime( System.currentTimeMillis() - 5000 );
        checker.setStartTime( System.currentTimeMillis() - 3000 );
        checker.setLastRunTime( System.currentTimeMillis() - 6000 );
        checker.checkServiceStatus();
        assertTrue( "Status not going to change due to grace period. But check the connection ", service.getStatus() == Service.SERVICE_UNKNOWN );

        // Iteration  that greater than both polling frequency and grace period
        updateLastAccessTime( 2000 );
        service.setStatus( Service.SERVICE_UNKNOWN );
        service.setLastUpdateTime( System.currentTimeMillis() - 35000 );
        checker.setStartTime( System.currentTimeMillis() - 3000 );
        checker.setLastRunTime( System.currentTimeMillis() - 6000 );
        checker.checkServiceStatus();
        assertTrue( "Status need to change to UP or DOWN ", ( service.getStatus() == Service.SERVICE_DOWN || service.getStatus() == Service.SERVICE_UP ) );


    }

    @Test public void testEligibleToUpdate() throws Exception
    {
        // check grace period
        //Service( "localhost", 3306, 25 * 1000, 5 * 1000 )  Grace period  set as 25*1000
        // polling frequency( 5*1000) is less than grace period (25 * 1000)

        ConnectionChecker checker = new ConnectionChecker( service, accessTimes );
        service.setLastUpdateTime( System.currentTimeMillis() );

        //check grace period with last update time set as current time
        boolean b = checker.eligibleToUpdate();
        assertFalse( "Should be false grace period is grater than (current time - last update time)   ", b );

        //check grace period with last update time set as (current time - 24990) . Still less than grace period (25000)
        service.setLastUpdateTime( System.currentTimeMillis() - 24990 );
        b = checker.eligibleToUpdate();
        assertFalse( "Still Should be false grace period is grater than (current time -  last update time)   ", b );

        //check grace period with last update time set as (current time - 25010) . Now grater than grace period (25000)
        service.setLastUpdateTime( System.currentTimeMillis() - 25010 );
        b = checker.eligibleToUpdate();
        assertTrue( "Now Should be true grace period is less than (current time -  last update time)   ", b );


    }

    @Test public void testEligibleToRun() throws Exception
    {
        // check polling frequency
        //Service( "localhost", 3306, 25 * 1000, 5 * 1000 )  polling frequency  set as 5*1000

        ConnectionChecker checker = new ConnectionChecker( service, accessTimes );
        checker.setLastRunTime( System.currentTimeMillis() );
        checker.setStartTime( System.currentTimeMillis() );

     /* to make sue eligibility check only depend on  polling frequency
        this is to make it true for "any service  should not poll  more frequently than once a second" */
        updateLastAccessTime( 2000 );


        //check polling frequency with last run time set as current time
        boolean b = checker.eligibleToRun();
        assertTrue( "Should be true since initiate of service   ", b );

        updateLastAccessTime( 2000 );
        //check polling frequency with last run time set as (current time - 4990) . less than polling frequency (5000)
        checker.setLastRunTime( System.currentTimeMillis() - 4990 );
        checker.setStartTime( System.currentTimeMillis() - 1000 );
        b = checker.eligibleToRun();
        assertFalse( " Should be false polling frequency is grater than (current time - last check time)   ", b );

        updateLastAccessTime( 2000 );
        //check polling frequency with last run time set as (current time - 5010) . Now grater than polling frequency (5000)
        checker.setLastRunTime( System.currentTimeMillis() - 5010 );
        checker.setStartTime( System.currentTimeMillis() - 1000 );
        b = checker.eligibleToRun();
        assertTrue( "Now Should be true polling frequency is less than (current time - last check time)   ", b );


        // validate with service access time to fulfill "any service  should not poll  more frequently than once a second"

        // to make sure polling frequency condition true
        checker.setLastRunTime( System.currentTimeMillis() - 5100 );
        checker.setStartTime( System.currentTimeMillis() - 1000 );

        // set service access time to current time
        updateLastAccessTime( 0 );
        b = checker.eligibleToRun();
        assertFalse( "Should be false last access time of service is less than 1s   ", b );

        // set service access time to (current time - 990) . still less than 1s
        updateLastAccessTime( 990 );
        b = checker.eligibleToRun();
        assertFalse( "still should be false last access time of service is less than 1s   ", b );

        // set service access time to (current time - 1010). Now grater than 1s
        updateLastAccessTime( 1010 );
        b = checker.eligibleToRun();
        assertTrue( "Now should be true last access time of service is greater than 1s   ", b );

    }

    private Service getService()
    {
        Service service = new Service( "localhost", 3306, 25 * 1000, 5 * 1000 );
        return service;
    }

    private ServiceAccessHandler getHandler()
    {
        ServiceAccessHandler handler = new ServiceAccessHandler( false, System.currentTimeMillis() );
        return handler;
    }

    private void updateLastAccessTime( long timeInMills )
    {
        accessTimes.setLastRunTime( System.currentTimeMillis() - timeInMills );
        accessTimes.setLock( false );
    }
}