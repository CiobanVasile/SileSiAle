package tasks.model;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public abstract class TaskList implements Iterable<Task>, Serializable {
    public abstract void add( Task task );

    public abstract boolean remove( Task task );

    public abstract int size( );

    public abstract Task getTask( int index );

    public abstract List<Task> getAll( );
}
