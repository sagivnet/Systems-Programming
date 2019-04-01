package bgu.spl181.net.api;

import bgu.spl181.net.api.bidi.BidiConnectionHandler;

public interface Connections <T>
{
    
    boolean send (int connectionId, T msg);
    
    void broadcast (T msg);
    
    void disconnect (int connectionId);
    
    void connect (int connectionId , BidiConnectionHandler connectionHandler);
}