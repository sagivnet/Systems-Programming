package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.Computer;

import bgu.spl.a2.sim.SuspendingMutex;
import bgu.spl.a2.sim.Warehouse;


import java.util.ArrayList;
import java.util.List;



/**                      Action for Actor: Department */

public class CheckAdministrativeObligationsAction extends Action
{
    //Fields
    private List<String> students;
    private String computerType;
    private List<String> conditions;
    private SuspendingMutex mutex;
    private Warehouse warehouse;
    private Promise<Computer> promise;
    
    //Methods
    public CheckAdministrativeObligationsAction
    (List<String> students, String computerType, List<String> conditions,Warehouse warehouse)
    {
        actionName = "Administrative Check";
        
        this.computerType = computerType;
        this.students = students;
        this.conditions = conditions;
        this.warehouse = warehouse;
    }
    @Override
    protected void start ()
    {
        
        mutex = warehouse.getMutex(computerType);
        promise = mutex.down();
        
        callback whenPCisFree = ()->    //PC is ours, add Actions to students
            {
                
                ArrayList<Action> actions = new ArrayList<> ();
                Computer pc = promise.get();
                
                for (int i=0 ; i<students.size () ; i++)
                {
                    // create new action for each student
                    Action forStudent = new signYourSelfAction(conditions,pc);
                    actions.add(forStudent);
                }
                
                //creates a callback for continuation
                callback continueWork = ()->
                {
                    //finished my work with the computer
                    mutex.up();
    
                    complete (true);
                    
                };
             
                then (actions, continueWork);
                
                 for (int i = 0; i<students.size () ; i++)
                {
                    Action action = actions.get (i);
                    String student = students.get (i);
                    PrivateState studentState = pool.getActorPrivateState (student);
                    
                    sendMessage (action,student, studentState);
                }
    
                //then (actions, continueWork);
            };
            
        promise.subscribe(whenPCisFree);
        //mutex.up ();
    
    }
}

