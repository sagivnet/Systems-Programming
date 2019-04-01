
package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;


/**                      Action for Actor: Student */


public class removeCourseFromStudentAction extends Action
{
    //Fields
    private String courseName;
    
    //Methods
    public removeCourseFromStudentAction(String courseName)
    {
        actionName = "remove Course From Student";
        
        this.courseName = courseName;
    }
    
    @Override
    protected void start ()
    {

        StudentPrivateState myState =  (StudentPrivateState) actorState;
        complete(myState.unregister(courseName));

    }
}