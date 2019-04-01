package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.callback;

import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;


import java.util.ArrayList;


/**                      Action for Actor: Department */

public class CloseACourseAction extends Action
{
    //Fields
    private String courseName;
    private ArrayList<Action> IDepOn;
    
    //Methods
    public CloseACourseAction
    (String courseName)
    {
        actionName = "Close Course";
        this.courseName = courseName;
        IDepOn = new ArrayList<Action> ();
    }
    
    
    @Override
    protected void start ()
    {

        
        DepartmentPrivateState myState =  (DepartmentPrivateState) actorState;
        
        //remove course from my list, TRY AGAIN if course doesn't exist
        if(!myState.removeCourse(courseName))
        {
            pool.submitMyself(this,actorId);
            return;
        }
        
        
        //create action
        Action removeStudents = new removeAllStudentsFromCourseAction();
        
        //submit action
        CoursePrivateState courseState = (CoursePrivateState) pool.getActorPrivateState(courseName);
        
        
        //add action to my I Depends On list
        IDepOn.add(removeStudents);
        //creates a callback for continuation
        callback continueWork = () ->
        {
            complete(removeStudents.getResult().get());
        };
        
        then(IDepOn, continueWork);
        
        sendMessage(removeStudents, courseName, courseState);
        
    }
}