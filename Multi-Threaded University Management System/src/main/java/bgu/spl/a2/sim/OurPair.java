package bgu.spl.a2.sim;

import bgu.spl.a2.PrivateState;

public class OurPair
{
    //Field
    private String name;
    private PrivateState state;

    //Methods
    public OurPair (String name, PrivateState state)
    {
        this.name = name;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public PrivateState getState() {
        return state;
    }
}
