package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;


/**                      Action for Actor: Department */

public class AddStudentAction extends Action
{
    //Fields
    private String studentName;
    
    //Methods
    public AddStudentAction (String studentName)
    {
        actionName = "Add Student";
        this.studentName=studentName;
    }
    
    @Override
    protected void start ()
    {

        
        DepartmentPrivateState myState =  (DepartmentPrivateState) actorState;
        //create Actor representing the new Student
        StudentPrivateState studentState = new StudentPrivateState ();
        
        pool.submit (null,studentName,studentState);


        
        myState.getStudentList().add (studentName);

    
        complete (true);
    }
}