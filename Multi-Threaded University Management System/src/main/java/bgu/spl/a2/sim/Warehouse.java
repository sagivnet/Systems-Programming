package bgu.spl.a2.sim;


import java.util.HashMap;

/**
 * represents a warehouse that holds a finite amount of computers
 *  and their suspended mutexes.
 * 
 */
public class Warehouse
{
	//Fields
    private Computer[] computers;
    private HashMap<String,SuspendingMutex> mutexes;
    
    //Methods
    public Warehouse( Computer[] computers )
    {
        this.computers = computers;
        this.mutexes = new HashMap<String, SuspendingMutex>();

        for (int i=0 ; i<computers.length ;i++)
            mutexes.put(computers[i].getType(), new SuspendingMutex(computers[i]));
    }

    public SuspendingMutex getMutex(String type) { return mutexes.get(type); }
}
