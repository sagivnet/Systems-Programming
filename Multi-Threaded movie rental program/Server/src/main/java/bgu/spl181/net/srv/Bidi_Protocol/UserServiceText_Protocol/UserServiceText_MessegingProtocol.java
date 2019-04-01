package bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol;
import bgu.spl181.net.srv.Bidi_Protocol.Bidi_MessegingProtocol;

public abstract class UserServiceText_MessegingProtocol extends Bidi_MessegingProtocol<String>
{
    //------------------------------------------------------------------------------------------------------------------
    //                                           UserServiceText Messaging Protocol
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Fields                                                   */
    //------------------------------------------------------------------------------------------------------------------
    protected UserServiceText_User user = null;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Methods                                                  */
    //------------------------------------------------------------------------------------------------------------------
    public void process(String message)
    {
        String cmd;
        int spaceIdx = message.indexOf(' ');

        //  Command parsing
        if(spaceIdx == -1)
            cmd = message;
        else
        {
            cmd = message.substring(0, message.indexOf(' '));
            message = message.substring(spaceIdx+1);
        }

        if(cmd.equals("SIGNOUT"))
            signOut (message);

        else if(cmd.equals("REGISTER"))
            register (message);

        else if(cmd.equals("LOGIN"))
            logIn (message);

        else if(cmd.equals("REQUEST"))
            request (message);
        
        else
            //  Error: Unknown command
            connections.send (connectionId, errorMessage(cmd,"Unknown command"));
    }
    //------------------------------------------------------------------------------------------------------------------
    /**                                                           Sign Out                                            */
    //------------------------------------------------------------------------------------------------------------------
    protected void signOut(String message)
    {
        if(!isLoggedIn())
        {
            //  Error: User is not logged in
            connections.send (connectionId, errorMessage("signout","User is not logged in"));
            return;
        }
        user.setLoggedIn (false);
        user = null;
        connections.send (connectionId, "ACK signout succeeded");
        connections.disconnect (connectionId);
        shouldTerminate = true;
    }
    //------------------------------------------------------------------------------------------------------------------
    /**                                                           Register                                            */
    //------------------------------------------------------------------------------------------------------------------
    protected void register(String message)
    {
        //Search for the first space without the cmd
        int spaceIdx = message.indexOf(" ");
        if(spaceIdx == -1)
        {
            //  Error: Wrong input: username is missing
            connections.send(connectionId, errorMessage("registration","Wrong input: something is missing"));
            return;
        }

        String username = message.substring(0, spaceIdx);
        
        message = message.substring(spaceIdx+1);
        spaceIdx = message.indexOf(" ");
        
        if(spaceIdx == -1)
        {
            //  Error: Wrong input : password is missing
            connections.send(connectionId, errorMessage("registration","Wrong input : something is missing"));
            return;
        }

        String password = message.substring(0, spaceIdx);

        
        //  Error: User is already registered
        if( ((UserServiceText_SharedData) sharedData).users.containsKey (username))
        {
            connections.send(connectionId, errorMessage("registration","User is already registered"));
            return;
        }
        
        //  User allocation according to RunTime Instance
        this.allocateUser(username,password,message);
    }
    //------------------------------------------------------------------------------------------------------------------
    //  Override this method at each sub class
    private void allocateUser(String username,String password, String message)
    {
        //  Allocate new "normal" user
        user = new UserServiceText_User (username,password,"normal");
    
        //-------------------------------------------Synchronization-----------------------------------------------
                                 ((UserServiceText_SharedData) sharedData).addUser (user);
        //---------------------------------------------------------------------------------------------------------
        
        //  Ack: registration succeed
        connections.send(connectionId, ackMessageSuccess ("registration"));
    }
    //------------------------------------------------------------------------------------------------------------------
    /**                                                           Log In                                              */
    //------------------------------------------------------------------------------------------------------------------
    protected void logIn(String message)
    {
        //Search for the first space without the cmd
        int spaceIdx = message.indexOf(" ");
        if(spaceIdx == -1)
        {
            //  Error: Wrong input : userName is missing
            connections.send(connectionId, errorMessage("login","Wrong input : something is missing"));
            return;
        }

        String username = message.substring(0, spaceIdx);
        String password;
        
        if(spaceIdx < message.length () - 1)
             password =  message.substring(spaceIdx+1);
        else
        {
            //  Error: Wrong input : password is missing
            connections.send(connectionId, errorMessage("login","Wrong input : something is missing"));
            return;
        }
    
        if(isLoggedIn ())
        {
            //  Error: User is already logged in
            connections.send(connectionId, errorMessage("login","User is already logged in"));
            return;
        }
        
        //----------------------------------------Synchronization--------------------------------------------------
              UserServiceText_User userHolder = ((UserServiceText_SharedData)sharedData).getUserByName (username);
        //---------------------------------------------------------------------------------------------------------
       if (userHolder != null)
       {
           if (!password.equals (userHolder.getPassword ()))
           {
               //  Error: Password is incorrect
               connections.send(connectionId, errorMessage("login","Password is incorrect"));
               return;
           }
    
           if (userHolder.isLoggedIn())
           {
               //  Error: Password is incorrect
               connections.send(connectionId, errorMessage("login","User is already logged in"));
               return;
           }
           
           //  Ack: Sign In succeed
           connections.send(connectionId, ackMessageSuccess("login"));
           user = userHolder;
           user.setLoggedIn (true);
       }
       else
           //  Error: User is not registered
           connections.send(connectionId, errorMessage("login","User is not registered"));
    }
    //------------------------------------------------------------------------------------------------------------------
    protected String errorMessage (String msg, String reson) {return "ERROR " + msg + " failed: (" + reson + ")";}
    //------------------------------------------------------------------------------------------------------------------
    protected String ackMessageSuccess (String msg) {return "ACK " + msg + " success";}
    
    protected String ackMessageWithoutSuccess (String msg) {return "ACK " + msg ;}
    //------------------------------------------------------------------------------------------------------------------
    protected void broadcast (String messege) { this.connections.broadcast("BROADCAST " +messege); }
    //------------------------------------------------------------------------------------------------------------------
    protected boolean isLoggedIn () { return user != null; }
    //------------------------------------------------------------------------------------------------------------------
    protected abstract void request (String message);
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
}
    
    



