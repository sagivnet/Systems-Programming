
package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.bidi.BidiServer;
import bgu.spl181.net.srv.Bidi_EncoderDecoder.Bidi_LineEncoderDecoder;
import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.BlockBuster_Protocol.BlockBuster_MessegingProtocol;
import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.BlockBuster_Protocol.BlockBuster_Movie;
import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.BlockBuster_Protocol.BlockBuster_SharedData;
import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.BlockBuster_Protocol.BlockBuster_User;
import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.UserServiceText_User;
import bgu.spl181.net.srv.Bidi_Servers.Bidi_BaseServer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static bgu.spl181.net.impl.Tools.intializeMaps;
import static bgu.spl181.net.impl.Tools.writeToMovies;
import static bgu.spl181.net.impl.Tools.writeUsers;

public class TPCMain
{
    //------------------------------------------------------------------------------------------------------------------
    //                                                         TPC Main
    //------------------------------------------------------------------------------------------------------------------
    /**                                                        Methods                                                */
    //------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args)
    {
        final Map<String, BlockBuster_Movie> movies = new ConcurrentHashMap<> ();
        final Map<String, BlockBuster_User> users = new ConcurrentHashMap<> ();
        
        intializeMaps (users,movies);
    
        Map<String,UserServiceText_User> usersMap = new ConcurrentHashMap<> (users);
        
        Runnable writeUsers = ()-> writeUsers (usersMap);
        Runnable writeMovies = ()-> writeToMovies (movies);
        
        //                     Shared Combined Data - Singleton -
        final BlockBuster_SharedData data = new BlockBuster_SharedData (usersMap,writeUsers,movies, writeMovies);

        //  server creation from supplier

        int port = Integer.parseInt (args[0]);

        Bidi_BaseServer server = (Bidi_BaseServer) BidiServer.threadPerClient
                (
                
                port,
                
                ()->
                     {
                        BlockBuster_MessegingProtocol toReturn = new BlockBuster_MessegingProtocol ();
                        toReturn.setSharedData (data);
                        return toReturn;
                     },
                
                ()->    new Bidi_LineEncoderDecoder ()
                
                );
        
        // Server starts running..
        server.serve ();
    }
    //------------------------------------------------------------------------------------------------------------------
}
