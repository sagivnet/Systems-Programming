package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;

import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;

import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;


/**                      Action for Actor: Course */

public class ParticipatingInCourseAction extends Action
{
    //Fields
    private String studentName;
    private String grade;
    private Promise<Boolean> promise;
    private CoursePrivateState myState;
    
    private Action addCourseToStudent;
    //Methods
    
    public ParticipatingInCourseAction
            (String studentName, String grade)
    
    {
        actionName = "Participate In Course";
        this.studentName = studentName;
        this.grade = grade;
        
    }
    
    @Override
    protected void start ()
    {
        myState =  (CoursePrivateState) actorState;
        
        //Check if there's room for the student
        if(myState.getAvailableSpots()<=0)
        {
            complete(false);
            
            

            return;
        }
        


        
        //Save room
        myState.saveSpot ();
        
        //try to add this course to student's grades list
        addCourseToStudent = new addCourseToStudentAction (actorId, grade,myState.getPrerequisites());
        
        //creates a list of actions I depends on
        ArrayList<Action> IDepOn = new ArrayList<> ();
        IDepOn.add(addCourseToStudent);
        
        //creates a callback for continuation
        callback continueWork = ()->
        {
            boolean confirmed = (boolean)addCourseToStudent.getResult ().get ();
            
            if(confirmed)
                myState.register(studentName);
            
            else
                myState.unsaveSpot ();

            complete (confirmed);
        };
        
        then (IDepOn, continueWork);
        
        //submit action
        StudentPrivateState studentState = (StudentPrivateState) pool.getActorPrivateState(studentName);
        
        
        //  Prevent DeadLock
        if(pool.actorsLocks.get(studentName).get ()) {
            pool.actorsLocks.get (actorId).set (false);
        }
        
        promise = sendMessage (addCourseToStudent,studentName,studentState);
        
    }
}