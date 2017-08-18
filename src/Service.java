import Lib.Time;

import java.util.Calendar;

/**
 * Created by rukman on 8/16/2017.
 */
public class Service
{
    public static int SERVICE_UP = 1;
    public static int SERVICE_DOWN = 0;
    public static int SERVICE_OUTAGE = 2;
    public static int SERVICE_UNKNOWN = -1;
    public static long MIN_INTERVAL = 1000;
    private String ip;
    private int port;
    private long graceTime;
    private int status;
    private long checkFrequency;
    private Time outageFrom;
    private Time outageTo;
    private long lastUpdateTime;
    private int serviceId;

    public Service( String ip, int port, long graceTime, long checkFrequency )
    {
        this.ip = ip;
        this.port = port;
        this.graceTime = graceTime;
        this.checkFrequency = checkFrequency;
        this.status = Service.SERVICE_UNKNOWN;
    }

    public Service( String ip, int port, long graceTime, long checkFrequency, Time outageFrom, Time outageTo )
    {
        this.ip = ip;
        this.port = port;
        this.graceTime = graceTime;
        this.checkFrequency = checkFrequency;
        this.outageFrom = outageFrom;
        this.outageTo = outageTo;
        this.status = Service.SERVICE_UNKNOWN;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp( String ip )
    {
        this.ip = ip;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort( int port )
    {
        this.port = port;
    }

    public long getGraceTime()
    {
        return graceTime;
    }

    public void setGraceTime( long graceTime )
    {
        this.graceTime = graceTime;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus( int status )
    {
        this.status = status;
    }

    public long getCheckFrequency()
    {
        return checkFrequency;
    }

    public void setCheckFrequency( long checkFrequency )
    {
        this.checkFrequency = checkFrequency;
    }

    public Time getOutageFrom()
    {
        return outageFrom;
    }

    public void setOutageFrom( Time outageFrom )
    {
        this.outageFrom = outageFrom;
    }

    public Time getOutageTo()
    {
        return outageTo;
    }

    public void setOutageTo( Time outageTo )
    {
        this.outageTo = outageTo;
    }

    public boolean isServerUp()
    {
        return status == SERVICE_UP;
    }

    public boolean isServerDown()
    {
        return status == SERVICE_DOWN;
    }

    public boolean isServerOutage()
    {
        return status == SERVICE_OUTAGE;
    }

    public void setOutage( boolean outage )
    {
        if( outage )
        {
            updateStatus( Service.SERVICE_OUTAGE );
        }
    }


    public boolean checkOutage()
    {
        if( outageFrom == null || outageTo == null || !outageFrom.isValid() || !outageTo.isValid() )
        {
            return false;
        }
        Calendar currentTime = Calendar.getInstance();
        boolean outage = currentTime.after( outageFrom.getCalenderTime() ) && currentTime.before( outageTo.getCalenderTime() );
        setOutage( outage );


        return outage;
    }

    public long getOutagePeriod()
    {
        if( outageFrom != null && outageTo != null && outageFrom.isValid() && outageTo.isValid() )
        {
            Calendar currentCal = Calendar.getInstance();
            long considerFromTime = outageFrom.getCalenderTime().after( currentCal ) ? currentCal.getTimeInMillis() : outageFrom.getCalenderTime().getTimeInMillis();
            return outageTo.getCalenderTime().getTimeInMillis() - considerFromTime;
        }
        return -1;
    }


    public long getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    public void setLastUpdateTime( long lastUpdateTime )
    {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getKey()
    {
        return ip + "_" + port;
    }

    public void updateStatus( int newStatus )
    {
        if( this.status != newStatus )
        {
            // this implementation its just print the status change. We can introduce mechanism to pass to the caller
            System.out.println( "Service " + getKey() + "_" + serviceId + " Status Change from " + getServiceStatus( this.status ) + " to " + getServiceStatus( newStatus ) );
            setStatus( newStatus );
        }
        setLastUpdateTime( System.currentTimeMillis() );
    }

    public int getServiceId()
    {
        return serviceId;
    }

    public void setServiceId( int serviceId )
    {
        this.serviceId = serviceId;
    }

    private String getServiceStatus( int status )
    {
        String serviceStatus = "";
        switch( status )
        {
            case -1:
                serviceStatus = " UNKNOWN ";
                break;
            case 0:
                serviceStatus = " DOWN ";
                break;
            case 1:
                serviceStatus = " UP ";
                break;
            case 2:
                serviceStatus = " OUTAGE ";
                break;
            default:
                serviceStatus = " N/A ";
                break;
        }
        return serviceStatus;
    }
}
