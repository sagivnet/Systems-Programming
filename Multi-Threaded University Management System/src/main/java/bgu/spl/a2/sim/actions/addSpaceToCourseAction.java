

package bgu.spl.a2.sim.actions;
        
        import bgu.spl.a2.Action;

        import bgu.spl.a2.sim.privateStates.CoursePrivateState;





/**                      Action for Actor: Course */


public class addSpaceToCourseAction extends Action
{
    //Fields
    private Integer spaces;
    
    //Methods
    
    public addSpaceToCourseAction(Integer spaces)
    {
        actionName = "Add Spaces";
        this.spaces = spaces;
    }
    
    @Override
    protected void start() {

        
        CoursePrivateState myState = (CoursePrivateState) actorState;
        //Update available spaces
        myState.addToAvailableSpots(spaces);
        complete (true);
        


        }
}
