package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.Promise;
import bgu.spl.a2.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**                      Action for Actor: Student */


public class PreferencesListAction extends Action
{
    //Fields
    private List<String> coursesName;
    private List<String> grades;
    private AtomicInteger counter;
    private Promise<Boolean> promise;
    private ArrayList<Action> IDepOn;
    private Action register;
    private String courseName;
    private PrivateState state;
    private callback continueWork;

    //Methods
    public PreferencesListAction( List<String> coursesName, List<String> grades)
    {
        actionName = "Register With Preferences";
        
        this.coursesName = coursesName;
        this.grades = grades;
        counter = new AtomicInteger (0);
    }

    @Override
    protected void start ()
    {
        
        register = new ParticipatingInCourseAction(actorId,grades.get(counter.get ()));
        courseName = coursesName.get(counter.get ());
        state = pool.getActorPrivateState(courseName);
        
        //creates a list of actions I depends on
        IDepOn = new ArrayList<> ();
        IDepOn.add(register);

        // creates a callback for continuation

        continueWork = ()->
            {
                counter.incrementAndGet ();
                boolean confirmed = promise.get();
                
                if(confirmed)
                {
                    complete(true);
                }
                else
                {
                    if(counter.get () == coursesName.size())
                    {
                        complete(false);
                    }

                    else
                    {
                        register = new ParticipatingInCourseAction(actorId,grades.get(counter.get ()));
                        courseName = coursesName.get(counter.get ());
                        state = pool.getActorPrivateState(courseName);

                        //creates a list of actions I depends on
                        IDepOn = new ArrayList<Action> ();
                        IDepOn.add(register);
                        
                        then (IDepOn, continueWork);
    
                        //submit action
                        promise = sendMessage(register,courseName,state);
                    }
                }
            };

       // then (IDepOn, continueWork);
    
        //submit action
    
        if(pool.actorsLocks.get(courseName).get ())
            pool.actorsLocks.get(actorId).set(false);
        
        promise = sendMessage(register,courseName,state);
    
        then (IDepOn, continueWork);
    }
}