package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.List;

/**                      Action for Actor: Student */


public class addCourseToStudentAction extends Action
{
    //Fields
    private String courseName;
    private String grade;
    private List<String> prerequisites;
    
    //Methods
    
    public addCourseToStudentAction(String courseName, String grade, List<String> prerequisites)
    {
        actionName = "add Course To Student";
        this.courseName = courseName;
        this.grade = grade;
        this.prerequisites = prerequisites;
    }
    
    @Override
    protected void start ()
    {
        
        StudentPrivateState myState =  (StudentPrivateState) actorState;

        if(myState.canParticipate(prerequisites))
        {
            myState.addGrade(courseName,grade);
            complete(true);

        }
        else
            {
            complete(false);
        }
        
    }
}