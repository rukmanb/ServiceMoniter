import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rukman on 8/17/2017.
 */
public class Monitor
{
    private static ConcurrentHashMap<String, ServiceAccessHandler> serviceAccessTimes;
    private static Vector<Service> services;
    private static HashMap<Integer, ExecutorService> checkerMap;
    private static int serviceId;

    public static void init()
    {
        serviceAccessTimes = new ConcurrentHashMap<>();
        services = new Vector<>();
        checkerMap = new HashMap<>();
    }

    public static int registerService( Service service )
    {

        if(!validateService( service ))
        {
            return  -1;
        }
        service.setServiceId( serviceId++ );
        services.add( service );
        checkHandler( service );

        ConnectionChecker checker = new ConnectionChecker( service, serviceAccessTimes.get( service.getKey() ) );


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit( checker );
        checkerMap.put( service.getServiceId(), executorService );

        return service.getServiceId();

    }

    private static boolean validateService(Service service)
    {

        return !( service.getIp() == null || service.getIp().trim().length() == 0 || service.getPort() <= 0 || service.getGraceTime() <= 0 || service.getCheckFrequency() <= 0 );

    }
    private static void checkHandler( Service service )
    {
        if( !serviceAccessTimes.containsKey( service.getKey() ) )
        {
            ServiceAccessHandler handler = new ServiceAccessHandler( false, System.currentTimeMillis() );
            serviceAccessTimes.put( service.getKey(), handler );
        }
    }

    public static void stopService( int id )
    {
        ExecutorService executorService = checkerMap.get( id );
        if( executorService != null )
        {
            System.out.println("ask to shutdown");
            executorService.shutdownNow();
        }
    }

    public ConcurrentHashMap<String, ServiceAccessHandler> getServiceAccessTimes()
    {
        return serviceAccessTimes;
    }

    public void setServiceAccessTimes( ConcurrentHashMap<String, ServiceAccessHandler> serviceAccessTimes )
    {
        this.serviceAccessTimes = serviceAccessTimes;
    }

    public static Vector<Service> getServices()
    {
        return services;
    }

    public static void setServices( Vector<Service> services )
    {
        Monitor.services = services;
    }
}
