
package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.callback;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;


import java.util.ArrayList;
import java.util.List;




/**                      Action for Actor: Course */


public class removeAllStudentsFromCourseAction extends Action {
    //Fields
    private ArrayList<Action> IDepOn;
    
    //Methods
    
    public removeAllStudentsFromCourseAction()
    {
        actionName = "remove All Students from course";
        
        IDepOn = new ArrayList<>();
    }
    
    
    @Override
    protected void start() {

        CoursePrivateState myState = (CoursePrivateState) actorState;
        List<String> students = myState.getRegStudents();
        
        //Update available spaces
        myState.setAvailableSpots(-1);
        
        //Ask all student to remove this course from their lists
        for (int i=0; i<students.size () ; i++)
        {
            //creates actions
            Action removeMe = new removeCourseFromStudentAction (actorId);
            IDepOn.add (removeMe);
        }
    
        //creates a callback for continuation
        callback continueWork = () ->
        {
        
            boolean confirmed = true;
        
            for (Action action : IDepOn)
                if (!(boolean) action.getResult ().get ()) {
                    confirmed = false;
                    break;
                }
        
            complete (confirmed);
        };
        
            then(IDepOn, continueWork);
    
        for (int i=0 ; i<IDepOn.size () ; i++)
        {
            String student = students.get (i);
            sendMessage (IDepOn.get (i), student, pool.getActorPrivateState(student));
        }
        
    }
}