package bgu.spl181.net.srv.Bidi_Connections;

import bgu.spl181.net.api.Connections;
import bgu.spl181.net.api.bidi.BidiConnectionHandler;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;


public class Bidi_Connections <T>implements Connections<T>
{
    //------------------------------------------------------------------------------------------------------------------
    //                                                   Bidi Connections
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Fields                                                   */
    //------------------------------------------------------------------------------------------------------------------
    private Map<Integer,BidiConnectionHandler>  activeClients;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Methods                                                  */
    //------------------------------------------------------------------------------------------------------------------
    public Bidi_Connections ()
    {
        activeClients = new WeakHashMap<> ();
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean send (int connectionId, T msg)
    {
        BidiConnectionHandler client = activeClients.get (connectionId);
        boolean isActive = client.isConnected ();
        
        if (isActive)
            client.send (msg);
        
        return isActive;
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void broadcast (T msg)
    {
        Iterator    < Map.Entry<Integer,BidiConnectionHandler> >  it =  activeClients.entrySet ().iterator ();
        
        while (it.hasNext ())
        {
            BidiConnectionHandler client = it.next ().getValue ();
            client.send (msg);
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void disconnect (int connectionId)
         { activeClients.remove (connectionId); }
    //------------------------------------------------------------------------------------------------------------------
    public void connect (int connectionId , BidiConnectionHandler connectionHandler)
            { activeClients.put (connectionId,connectionHandler);}
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
}
