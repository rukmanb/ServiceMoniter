import java.io.IOException;
import java.net.Socket;

/**
 * Created by rukman on 8/17/2017.
 */
public class ConnectionChecker implements Runnable
{
    private Service service;
    private ServiceAccessHandler handler;
    private long lastRunTime;
    private long startTime;

    @Override public void run()
    {
        lastRunTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();

        while( true )
        {
            checkServiceStatus();
            if( Thread.currentThread().isInterrupted() )
            {
                return;
            }
        }
    }

    public void checkServiceStatus()
    {
        if( !service.checkOutage() )
        {
            int connected = service.getStatus();
            if( eligibleToRun() )
            {
                connected = checkConnection() ? Service.SERVICE_UP : Service.SERVICE_DOWN;
                handler.releaseLock();
                lastRunTime = System.currentTimeMillis();
            }
            if( eligibleToUpdate() )
            {
                if( validateUpdateAndRunTime() )
                {
                    connected = checkConnection() ? Service.SERVICE_UP : Service.SERVICE_DOWN;
                    handler.releaseLock();
                }
                service.updateStatus( connected );
            }

        }
        else
        {
            //sleep the thread during outage period
            try
            {
                long outagePeriod = service.getOutagePeriod();
                if( outagePeriod > 0 )
                {
                    Thread.sleep( outagePeriod );
                }

            }
            catch( InterruptedException e )
            {
                // need to log error with logger framework
                //    e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public ConnectionChecker( Service service, ServiceAccessHandler serviceHandler )
    {
        this.service = service;
        this.handler = serviceHandler;
    }

    public boolean validateUpdateAndRunTime()
    {

        return ( service.getLastUpdateTime() > lastRunTime ) && handler.checkEligibility();
    }

    public boolean eligibleToUpdate()
    {

        long currentTime = System.currentTimeMillis();
        return currentTime - service.getLastUpdateTime() > service.getGraceTime();

    }

    public boolean eligibleToRun()
    {
        boolean eligibility = false;
        long currentTime = System.currentTimeMillis();

        if( ( currentTime - lastRunTime >= service.getCheckFrequency() ) || ( currentTime - startTime == 0 ) )
        {
            eligibility = true;
        }
        else
        {
            try
            {
                if( !Thread.currentThread().isInterrupted() )
                {
                    Thread.sleep( service.getCheckFrequency() );
                }
            }
            catch( InterruptedException e )
            {
                Thread.currentThread().interrupt();
                // need to log error with logger framework
                // e.printStackTrace();
            }
        }

        // check whether same service ( ip and port) should not poll  more frequently than once a second.
        eligibility = eligibility && handler.checkEligibility();

        return eligibility;

    }


    private boolean checkConnection()
    {
        boolean connected = false;
        Socket socket = null;
        try
        {
            socket = new Socket( service.getIp(), service.getPort() );
            if( socket.isConnected() )
            {
                connected = true;
            }

        }
        catch( IOException e )
        {
            // need to log error with logger framework
            // e.printStackTrace();
        }
        finally
        {
            if( socket != null )
            {
                try
                {
                    socket.close();
                }
                catch( IOException e )
                {
                    // need to log error with logger framework
                    //e.printStackTrace();
                }
            }

        }
        return connected;
    }


    public ServiceAccessHandler getHandler()
    {
        return handler;
    }

    public void setHandler( ServiceAccessHandler handler )
    {
        this.handler = handler;
    }

    public long getLastRunTime()
    {
        return lastRunTime;
    }

    public void setLastRunTime( long lastRunTime )
    {
        this.lastRunTime = lastRunTime;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime( long startTime )
    {
        this.startTime = startTime;
    }
}
