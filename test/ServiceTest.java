import Lib.Time;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Created by rukman on 8/18/2017.
 */
public class ServiceTest
{

    @Test public void testCheckOutage() throws Exception
    {
        Service service = getService();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get( Calendar.HOUR_OF_DAY );
        int minute = calendar.get( Calendar.MINUTE );


        //  current time fall into outage period
        Time from = new Time( hour, ( minute - 10 ) );
        Time to = new Time( hour, ( minute + 10 ) );

        service.setOutageFrom( from );
        service.setOutageTo( to );


        boolean b = service.checkOutage();
        assertTrue( "Within Outage", b );

        //  current time after the outage period
        from = new Time( ( hour - 1 ), minute );
        to = new Time( hour, ( minute - 10 ) );

        service.setOutageFrom( from );
        service.setOutageTo( to );


        b = service.checkOutage();
        assertFalse( "Out of  Outage", b );

        //  current time before the outage period
        from = new Time( ( hour + 1 ), minute );
        to = new Time( ( hour + 2 ), minute );

        service.setOutageFrom( from );
        service.setOutageTo( to );

        b = service.checkOutage();
        assertFalse( "Out of  Outage", b );


    }

    public Service getService()
    {
        return new Service( "localhost", 3306, 25 * 1000, 5 * 1000 );
    }
}


