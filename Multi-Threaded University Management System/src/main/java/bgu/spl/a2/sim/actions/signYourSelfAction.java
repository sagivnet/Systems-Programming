
package bgu.spl.a2.sim.actions;

        import bgu.spl.a2.Action;

        import bgu.spl.a2.sim.Computer;
        import bgu.spl.a2.sim.privateStates.StudentPrivateState;

        import java.util.List;

/**                      Action for Actor: Student */


public class signYourSelfAction extends Action
{
    //Fields
    private List<String> conditions;
    private Computer pc;
    
    //Methods
    public signYourSelfAction(List<String> conditions, Computer pc)
    {
        actionName = "sign Yourself Action";
        
        this.conditions = conditions;
        this.pc = pc;
    }

    @Override
    protected void start ()
    {

        
        StudentPrivateState myState =  (StudentPrivateState) actorState;
        
        if (pc.lockPC())
        {
            myState.setSignature(pc.checkAndSign(conditions, myState.getGrades()));
    
            pc.unlockPC();
            complete(true);
        }
        
        else {
            pool.submitMyself (this, actorId);
        }
        
    }
}