package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;

/**                      Action for Actor: Course */

public class UnregisterAction extends Action
{
    //Fields
    private String studentName;
    
    //Methods
    public UnregisterAction(String studentName)
    {
        actionName = "Unregister";
        this.studentName = studentName;
    }
    
    @Override
    protected void start ()
    {

        
        CoursePrivateState myState =  (CoursePrivateState) actorState;
        
        if (!myState.unregister (studentName))
        {
            pool.submitMyself(this,actorId);
            return;
        }
        
        //Creates action
        Action removeCourseFromStudent = new removeCourseFromStudentAction (actorId);
        
        //creates a list of actions I depends on
        ArrayList<Action> IDepOn = new ArrayList<> ();
        IDepOn.add(removeCourseFromStudent);
        
        //creates a callback for continuation
        callback continueWork = ()->
        {
            boolean confirmed = (boolean)removeCourseFromStudent.getResult().get();
            if (!confirmed)
                //try again
                pool.submit (this , actorId , actorState);
           
            else
            {
                complete (true);
            }
        };


        then (IDepOn, continueWork);
        //submit action
        StudentPrivateState studentState = (StudentPrivateState) pool.getActorPrivateState(studentName);
    
        //  Prevent DeadLock
        if(pool.actorsLocks.get(studentName).get ()) {
            pool.actorsLocks.get (actorId).set (false);

        }
        
        sendMessage (removeCourseFromStudent,studentName,studentState);

    }
}