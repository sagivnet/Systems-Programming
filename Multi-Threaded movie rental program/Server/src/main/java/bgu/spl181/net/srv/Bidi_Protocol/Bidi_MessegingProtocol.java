package bgu.spl181.net.srv.Bidi_Protocol;

import bgu.spl181.net.api.Connections;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;


public abstract class  Bidi_MessegingProtocol<T> implements BidiMessagingProtocol<T>
{
    //------------------------------------------------------------------------------------------------------------------
    //                                               Bidi Messaging Protocol
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Fields                                                   */
    //------------------------------------------------------------------------------------------------------------------
    protected int connectionId;
    protected Connections connections;
    protected Bidi_SharedData sharedData;
    protected boolean shouldTerminate = false;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Methods                                                  */
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void start (int connectionId, Connections connections)
    {
        this.connectionId = connectionId;
        this.connections = connections;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public abstract void process (T message) ;
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean shouldTerminate ()
    {
        return shouldTerminate;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void setSharedData(Bidi_SharedData data)
          { sharedData = data; }
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
}
