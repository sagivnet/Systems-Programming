package bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol;

import bgu.spl181.net.srv.Bidi_Protocol.Bidi_SharedData;

import java.util.Map;

public class UserServiceText_SharedData extends Bidi_SharedData
{
    //------------------------------------------------------------------------------------------------------------------
    //                                              UserServiceText SharedData
    //------------------------------------------------------------------------------------------------------------------
    /**                                                     Fields                                                    */
    //------------------------------------------------------------------------------------------------------------------
    protected Map<String,UserServiceText_User> users;
    protected Runnable writeUsers;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                     Methods                                                   */
    //------------------------------------------------------------------------------------------------------------------
    public UserServiceText_SharedData ( Runnable writeUsers )
    {
        super();
        this.writeUsers = writeUsers;
    }
    //------------------------------------------------------------------------------------------------------------------
    public UserServiceText_SharedData ( Map<String,UserServiceText_User> users, Runnable writeUsers )
    {
        this (writeUsers);
        this.users = users;
    }
    //------------------------------------------------------------------------------------------------------------------
    public void addUser(UserServiceText_User user)
    {
        synchronized (users)
        {
            users.put (user.getUsername (), user);
            
            writeUsers.run ();
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public boolean removeUser(UserServiceText_User user)
    {
        boolean removed = false;
        synchronized (users)
        {
            removed = users.remove (user) != null;
            
            writeUsers.run ();
        }
        return removed;
    }
    //------------------------------------------------------------------------------------------------------------------
    protected UserServiceText_User getUserByName(String userName)
    {
        return users.get (userName);
    }
    //------------------------------------------------------------------------------------------------------------------
    public boolean isContains (String userName)
    {
        return users.containsKey (userName);
    }
    //------------------------------------------------------------------------------------------------------------------
    
}
