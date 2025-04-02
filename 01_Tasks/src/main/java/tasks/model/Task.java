package tasks.model;

import org.apache.log4j.Logger;
import tasks.persistence.TaskFileHandler;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task implements Serializable {
    private String title;
    private Date time;
    private Date start;
    private Date end;
    private int interval;
    private boolean active;

    private static final Logger log = Logger.getLogger( Task.class.getName( ) );
    private static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );

    public static SimpleDateFormat getDateFormat( ) {
        return sdf;
    }

    public Task( String title, Date time ) {
        if ( time.getTime( ) < 0 ) {
            log.error( "time below bound" );
            throw new IllegalArgumentException( "Time cannot be negative" );
        }
        this.title = title;
        this.time = time;
        this.start = time;
        this.end = time;
    }

    public Task( Task original ) {
        this.title = original.title;
        this.time = ( Date ) original.time.clone( );
        this.start = ( Date ) original.start.clone( );
        this.end = ( Date ) original.end.clone( );
        this.interval = original.interval;
        this.active = original.active;
    }

    public Task( String title, Date start, Date end, int interval ) {
        if ( title.isEmpty()) {
            log.error( "title is empty" );
            throw new IllegalArgumentException( "Title cannot be empty" );
        }
        if ( start.getTime( ) < 0 || end.getTime( ) < 0 ) {
            log.error( "time below bound" );
            throw new IllegalArgumentException( "Time cannot be negative" );
        }
        if ( interval < 1 ) {
            log.error( "interval < than 1" );
            throw new IllegalArgumentException( "interval should me > 1" );
        }
        this.title = title;
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.time = start;
    }

    public String getTitle( ) {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public boolean isActive( ) {
        return this.active;
    }

    public void setActive( boolean active ) {
        this.active = active;
    }

    public Date getTime( ) {
        return time;
    }

    public void setTime( Date time ) {
        this.time = time;
        this.start = time;
        this.end = time;
        this.interval = 0;
    }

    public Date getStartTime( ) {
        return start;
    }

    public Date getEndTime( ) {
        return end;
    }

    public int getRepeatInterval( ) {
        return interval > 0 ? interval : 0;
    }

    public void setTime( Date start, Date end, int interval ) {
        this.time = start;
        this.start = start;
        this.end = end;
        this.interval = interval;

    }

    public boolean isRepeated( ) {
        return ( this.interval != 0 );

    }

    private Date getNextRepeatedTime( Date current ) {
        if ( current.before( start ) ) {
            return start;
        }

        long intervalMillis = interval * 1000L;
        for ( long timeIterator = start.getTime( ); timeIterator <= end.getTime( ); timeIterator += intervalMillis ) {
            Date timeAfter = new Date( timeIterator );
            if ( current.equals( timeAfter ) ) {
                return new Date( timeAfter.getTime( ) + intervalMillis );
            }
            if ( current.after( new Date( timeIterator - intervalMillis ) ) && current.before( timeAfter ) ) {
                return timeAfter;
            }
        }
        return null;
    }

    private Date getNextNonRepeatedTime( Date current ) {
        if ( current.before( time ) && isActive( ) ) {
            return time;
        }
        return null;
    }

    public Date nextTimeAfter( Date current ) {
        if ( current.after( end ) || current.equals( end ) ) {
            return null;
        }

        if ( isRepeated( ) && isActive( ) ) {
            return getNextRepeatedTime( current );
        }

        return getNextNonRepeatedTime( current );
    }

    //duplicate methods for TableView which sets column
    // value by single method and doesn't allow passing parameters
    public String getFormattedDateStart( ) {
        return sdf.format( start );
    }

    public String getFormattedDateEnd( ) {
        return sdf.format( end );
    }

    public String getFormattedRepeated( ) {
        if ( isRepeated( ) ) {
            String formattedInterval = TaskFileHandler.getFormattedInterval( interval );
            return "Every " + formattedInterval;
        } else {
            return "No";
        }
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass( ) != o.getClass( ) ) return false;

        Task task = ( Task ) o;

        if ( !time.equals( task.time ) ) return false;
        if ( !start.equals( task.start ) ) return false;
        if ( !end.equals( task.end ) ) return false;
        if ( interval != task.interval ) return false;
        if ( active != task.active ) return false;
        return title.equals( task.title );
    }

    @Override
    public int hashCode( ) {
        int result = title.hashCode( );
        result = 31 * result + time.hashCode( );
        result = 31 * result + start.hashCode( );
        result = 31 * result + end.hashCode( );
        result = 31 * result + interval;
        result = 31 * result + ( active ? 1 : 0 );
        return result;
    }

    @Override
    public String toString( ) {
        return "Task{" +
                "title='" + title + '\'' +
                ", time=" + time +
                ", start=" + start +
                ", end=" + end +
                ", interval=" + interval +
                ", active=" + active +
                '}';
    }
}