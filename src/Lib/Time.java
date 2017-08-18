package Lib;

import java.util.Calendar;

/**
 * Created by rukman on 8/16/2017.
 */
public class Time
{
    private int hour; // in 24 hour manner
    private int minute;
    private int dateOffset; // when it need specify next date time related to from time 22:00 PM - 02:00 AM

    public Time( int hour, int minute )
    {
        this.hour = hour;
        this.minute = minute;
        dateOffset = -1;
    }

    public Time( int hour, int minute, int dateOffset )
    {
        this.hour = hour;
        this.minute = minute;
        this.dateOffset = dateOffset;
    }

    public int getHour()
    {
        return hour;
    }

    public void setHour( int hour )
    {
        this.hour = hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public void setMinute( int minute )
    {
        this.minute = minute;
    }


    public Calendar getCalenderTime()
    {
        Calendar returnTime = Calendar.getInstance();
        returnTime.set( Calendar.HOUR_OF_DAY, hour );
        returnTime.set( Calendar.MINUTE, minute );
        returnTime.set( Calendar.SECOND, 0 );
        if( dateOffset > 0 )
        {
            returnTime.add( Calendar.DATE, dateOffset );
        }
        return returnTime;

    }

    public int getDateOffset()
    {
        return dateOffset;
    }

    public void setDateOffset( int dateOffset )
    {
        this.dateOffset = dateOffset;
    }

    public boolean isValid()
    {
        boolean valid = true;
        if( hour < 0 && hour > 23 )
        {
            valid = false;
        }
        else if( hour < 0 && hour > 60 )
        {
            valid = false;
        }
        return valid;
    }
}
