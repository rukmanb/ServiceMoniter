import Lib.Time;

import java.util.Calendar;

/**
 * Created by rukman on 8/17/2017.
 */
public class Caller
{
    public static void main( String ar[] )
    {
        try
        {
            Monitor.init();

            Service service = new Service( "localhost", 3306, 25 * 1000, 6 * 1000 );
            Service service2 = new Service( "localhost", 3306, 15 * 1000, 3 * 1000 );

            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get( Calendar.HOUR_OF_DAY );
            int minute = calendar.get( Calendar.MINUTE );


            Time from = new Time( hour, ( minute + 2 ) );
            Time to = new Time( hour, ( minute + 4 ) );
            service.setOutageFrom( from );
            service.setOutageTo( to );
            int i = Monitor.registerService( service );
            int j = Monitor.registerService( service2 );
            Thread.sleep( 5 * 60 * 1000 );
            Monitor.stopService( i );
            Monitor.stopService( j );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}

