/**
 * Created by rukman on 8/17/2017.
 */
public class ServiceAccessHandler
{
    private boolean lock;
    private long lastRunTime;

    public ServiceAccessHandler( boolean lock, long lastRunTime )
    {
        this.lock = lock;
        this.lastRunTime = lastRunTime;
    }

    public boolean isLock()
    {
        return lock;
    }

    public void setLock( boolean lock )
    {
        this.lock = lock;
    }

    public long getLastRunTime()
    {
        return lastRunTime;
    }

    public void setLastRunTime( long lastRunTime )
    {
        this.lastRunTime = lastRunTime;
    }

    public synchronized boolean checkEligibility()
    {
        boolean eligible = false;
        long currentTime = System.currentTimeMillis();
        if( !lock && currentTime - lastRunTime > Service.MIN_INTERVAL )
        {
            lock = true;
            eligible = true;
        }
        return eligible;
    }

    public synchronized void releaseLock()
    {
        lock = false;
        lastRunTime = System.currentTimeMillis();
    }
}
