package bgu.spl.a2.sim.actions;

import bgu.spl.a2.*;

import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;


import java.util.List;



/**                      Action for Actor: Department */


public class OpenCourseAction extends Action
{
    //Fields
    private String courseName;
    private Integer availableSpaces;
    private List<String> prerequisites;
    
    //Methods
    public OpenCourseAction (String courseName, Integer availableSpaces, List<String> prerequisites)
    {
        actionName = "Open Course";
        this.courseName = courseName;
        this.availableSpaces = availableSpaces;
        this.prerequisites = prerequisites;
       
    }
    @Override
    protected void start ()
    {
        
        DepartmentPrivateState myState =  (DepartmentPrivateState) actorState;
        //creates an Actor representing the new course
        CoursePrivateState courseState = new CoursePrivateState ();
        courseState.setAvailableSpots (availableSpaces);
        courseState.setPrerequisites (prerequisites);

        myState.getCourseList().add (courseName);

        pool.submit (null,courseName,courseState);

        complete (true);
    }
}