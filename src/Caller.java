import Lib.Time;

import java.util.Calendar;

/**
 * Created by rukman on 8/17/2017.
 */
public class Caller
{
    public  static void main(String ar[])
    {
        try
        {
            Monitor.init();

            Service service = new Service( "",3306, 0, 0  );
            Service service2 = new Service( "localhost",3306, 15*1000, 3*1000  );

            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get( Calendar.HOUR_OF_DAY );
            int minute = calendar.get( Calendar.MINUTE );


            //  current time fall into outage period
            Time from = new Time( hour, ( minute + 2 ) );
            Time to = new Time( hour, ( minute + 4 ) );
//                   int j = Monitor.registerService( service2 );
//            System.out.println( j );
            service.setOutageFrom( from );
            service.setOutageTo( to );
            int i = Monitor.registerService( service );
            System.out.println( i );
            System.out.println( " before " );
            Thread.sleep( 3 * 60 * 1000 );
            System.out.println( " after " );
            Monitor.stopService( i );
//            Monitor.stopService( j );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}
