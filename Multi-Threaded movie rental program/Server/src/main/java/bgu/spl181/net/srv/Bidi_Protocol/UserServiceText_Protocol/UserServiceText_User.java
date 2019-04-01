package bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol;

public class UserServiceText_User
{
    //------------------------------------------------------------------------------------------------------------------
    //                                                UserServiceText User
    //------------------------------------------------------------------------------------------------------------------
    /**                                                     Fields                                                    */
    //------------------------------------------------------------------------------------------------------------------
    protected String username;
    protected String password;
    protected String type;
    protected boolean isLoggedIn = false;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                     Methods                                                   */
    //------------------------------------------------------------------------------------------------------------------
    public UserServiceText_User (String username, String password, String type)
    {
        this.username = username;
        this.password = password;
        this.type = type;
    }
    
    public String getPassword () {
        return password;
    }
    
    public String getType () {
        return type;
    }
    
    public String getUsername () {
    
        return username;
    }
    
    public boolean isLoggedIn () {
        return isLoggedIn;
    }
    
    public void setLoggedIn (boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
}
