package bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.BlockBuster_Protocol;
import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.UserServiceText_MessegingProtocol;

import java.util.List;
import java.util.Vector;

public class BlockBuster_MessegingProtocol extends UserServiceText_MessegingProtocol {
    //------------------------------------------------------------------------------------------------------------------
    //                                           BlockBuster Messaging Protocol
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Fields                                                   */
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Methods                                                  */
    //------------------------------------------------------------------------------------------------------------------
    //                                                     -Register-                                                  /
    //------------------------------------------------------------------------------------------------------------------
    public void process(String message) {super.process (message);}
    //------------------------------------------------------------------------------------------------------------------
    protected void register (String message)
    {
        //Search for the first space without the cmd
        int spaceIdx = message.indexOf(" ");
        if(spaceIdx == -1)
        {
            //  Error: Wrong input: username is missing
            connections.send(connectionId, errorMessage("registration", "Wrong input: something is missing"));
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
        if( ((BlockBuster_SharedData) sharedData).isContains(username))
        {
            connections.send(connectionId, errorMessage("registration","User is already exist"));
            return;
        }
    
        //  User allocation according to RunTime Instance
        this.allocateUser(username,password,message);
    }
    //------------------------------------------------------------------------------------------------------------------
    private void allocateUser (String username, String password, String message) {
        //  Breaking message continuation
        if (!message.contains ("country=")) {
            //  Error: Wrong input : country= is missing
            connections.send (connectionId, errorMessage ("registration","Wrong input : something is missing"));
            return;
        }
    
        if (message.indexOf ('"') == -1 || message.lastIndexOf ('"') == message.indexOf ('"')) {
            //  Error: Wrong input : country is missing
            connections.send (connectionId, errorMessage ("registration","Wrong input : something is missing"));
            return;
        }
        
        
        String country = message.substring (message.indexOf ('"') + 1, message.lastIndexOf ('"'));
        
        
        //  Allocate new "normal" user
        BlockBuster_User user = new BlockBuster_User (username, password, "normal", country);
        
        //  ----------------------------------------Synchronization------------------------------------------------
                            ((BlockBuster_SharedData) sharedData).addUser (user);
        //  -------------------------------------------------------------------------------------------------------
        
        //  Ack: Registration succeed
        connections.send (connectionId, ackMessageSuccess ("registration"));
    }
    //------------------------------------------------------------------------------------------------------------------
    //                                                     -Request-                                                   /
    //------------------------------------------------------------------------------------------------------------------
    @Override
    protected void request (String message)
    {
        //- - - - - - - - - - - - - - - - - - - - - - -  User Requests - - - - - - - - - - - - - - - - - - - - - - - - -
        //___________________________________________________ours_______________________________________________________
        if (message.contains ("mymovies"))
            userMyMovies (message);
        
        else if (message.contains ("myinfo"))
            myinfo (message);
        //______________________________________________________________________________________________________________
        else if (message.contains ("balance"))
            userBalance (message);
    
        else if (message.contains ("info"))
            userInfo (message);
    
        else if (message.contains ("rent"))
            userRent (message);
    
        else if (message.contains ("return"))
            userReturnMovie (message);
    
        //- - - - - - - - - - - - - - - - - - - - - - - Admin Requests - - - - - - - - - - - - - - - - - - - - - - - - -
        
        else if (message.contains ("addmovie"))
            adminAddMovie (message);
    
        else if (message.contains ("remmovie"))
            adminRemMovie (message);
    
        else if (message.contains ("changeprice"))
            adminChangePrice (message);
    }
    //------------------------------------------------------------------------------------------------------------------
    //                                            (User) Request: My Info
    //------------------------------------------------------------------------------------------------------------------
    private void myinfo (String message)
    {
        if (!isLoggedIn ())
        {
            //  Error: User is not logged in
            connections.send (connectionId, errorMessage ("myinfo", "User is not logged in"));
            return;
        }
    
        String myInfo = ((BlockBuster_User)user).getInfo ();
    
        //  Ack: Info about all movies
        connections.send (connectionId, ackMessageWithoutSuccess (myInfo));
        return;
    }
    
    //------------------------------------------------------------------------------------------------------------------
    //                                            (User) Request: My Movies
    //------------------------------------------------------------------------------------------------------------------
    private void userMyMovies (String message)
    {
        if (!isLoggedIn ())
        {
            //  Error: User is not logged in
            connections.send (connectionId, errorMessage ("mymovies", "User is not logged in"));
            return;
        }
        
        String myMovies = ((BlockBuster_User)user).getMovies ();

        //  Ack: Info about all movies
        connections.send (connectionId, (myMovies));
        return;
    }
    
    //------------------------------------------------------------------------------------------------------------------
    //                                            (User) Request: Balance
    //------------------------------------------------------------------------------------------------------------------
    private void userBalance (String message)
    {
        //- - - - - - - - - - - - - - - - - - - - - - -  Conditions Check - - - - - - - - - - - - - - - - - - - - - - -
        
        if (!isLoggedIn ())
        {
            //  Error: User is not logged in
            connections.send (connectionId, errorMessage ("balance","User is not logged in"));
            return;
        }
        
        //- - - - - - - - - - - - - - - - - - - - - - -  Balance Info - - - - - - - - - - - - - - - - - - - - - - - - -
   
        if (message.equals ("balance info"))
        {
            // Ack: Current balance
            Integer balance = ((BlockBuster_User) user).getBalance ();
            connections.send (connectionId, "ACK balance " + balance);
        }
        
        //- - - - - - - - - - - - - - - - - - - - - - -  Balance Add - - - - - - - - - - - - - - - - - - - - - - - - - -
        
        else if (message.contains ("balance add"))
        {
            String toAdd = message.substring (message.lastIndexOf (' ')+1);
            
            //  ----------------------------------------Synchronization--------------------------------------------
                  String msg = ((BlockBuster_SharedData) sharedData).addToUserBalance (toAdd, user.getUsername ());
            //  ---------------------------------------------------------------------------------------------------
    
            if (msg.equals ("User is not exist"))
            {
                //  Error: User is not exist
                connections.send (connectionId, errorRequestMessage ("balance",msg));
                return;
            }
            if (msg.equals ("Negative amount"))
            {
                //  Error: User is not exist
                connections.send (connectionId, errorRequestMessage ("balance",msg));
                return;
            }
            //  Ack: Info about a specific movie
            connections.send (connectionId, ackMessageWithoutSuccess ("balance " + msg + " added " + toAdd));
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    //                                            (User) Request: Info about a Movie
    //------------------------------------------------------------------------------------------------------------------
    private void userInfo (String message)
    {
        //- - - - - - - - - - - - - - - - - - - - - - -  Conditions Check - - - - - - - - - - - - - - - - - - - - - - -
       
        if (!isLoggedIn ())
        {
            //  Error: User is not logged in
            connections.send (connectionId, errorRequestMessage ("info","User is not logged in"));
            return;
        }
    
        //- - - - - - - - - - - - - - - - - - - - - Info about all movies - - - - - - - - - - - - - - - - - - - - - - -
        
        if (message.equals ("info"))
        {
            List<String> allMovies = ((BlockBuster_SharedData) sharedData).getMoviesNames ();
            
            //  Ack: Info about all movies
            connections.send (connectionId, ackMessageWithoutSuccess ("info" + listToString (allMovies)));
            return;
        }
    
        //- - - - - - - - - - - - - - - - - - - -  Info about a specific movie  - - - - - - - - - - - - - - - - - - - -
    
        String movieName = message.substring (message.indexOf ('"')+1, message.lastIndexOf ('"'));

        //  ----------------------------------------Synchronization------------------------------------------------
                      String movieInfo = ((BlockBuster_SharedData) sharedData).getMovieInfo (movieName);
        //  -------------------------------------------------------------------------------------------------------
    
        if (movieInfo.equals (""))
        {
            //  Error: Movie does not exist
            connections.send (connectionId, errorRequestMessage ("info","Movie does not exist"));
            return;
        }
    
        //  Ack: Info about a specific movie
        connections.send (connectionId, ackMessageWithoutSuccess ("info " + movieInfo));
    }
    //------------------------------------------------------------------------------------------------------------------
    //                                            (User) Request: Rent Movie
    //------------------------------------------------------------------------------------------------------------------
    private void userRent (String message)
    {
        //- - - - - - - - - - - - - - - - - - - - - - -  Conditions Check - - - - - - - - - - - - - - - - - - - - - - -
        
        if (!isLoggedIn ())
        {
            //  Error: User is not logged in
            connections.send (connectionId, errorRequestMessage ("rent","User is not logged in"));
            return;
        }
    
        String movieName = message.substring (message.indexOf ('"')+1, message.lastIndexOf ('"'));
    
        //  ----------------------------------------Synchronization------------------------------------------------
                    String broadcastMsg = ((BlockBuster_SharedData) sharedData).rentMovie (movieName,user.getUsername ());
        //  -------------------------------------------------------------------------------------------------------
        if( ! broadcastMsg.substring (0,broadcastMsg.indexOf (' ')) .equals ("movie"))
        {
            //  Error: User or movie doesn't exist, or not enough money at user balance, or there's no available copy,
            //                                                                          or user already rent this movie.
            connections.send (connectionId, errorRequestMessage ("rent",broadcastMsg));
            return;
        }
        //  Ack: Renting the movie
        connections.send (connectionId, ackMessageSuccess ("rent " + '"' + movieName + '"'));
        broadcast ( broadcastMsg );
    }
    //------------------------------------------------------------------------------------------------------------------
    //                                            (User) Request: Return Movie
    //------------------------------------------------------------------------------------------------------------------
    private void userReturnMovie (String message)
    {
        //- - - - - - - - - - - - - - - - - - - - - - -  Conditions Check - - - - - - - - - - - - - - - - - - - - - - -
        if (!isLoggedIn ())
        {
            //  Error: User is not logged in
            connections.send (connectionId, errorRequestMessage ("return","User is not logged in"));
            return;
        }
    
        String movieName = message.substring (message.indexOf ('"')+1, message.lastIndexOf ('"'));
    
        //----------------------------------------Synchronization--------------------------------------------------
            String broadcastMsg = ((BlockBuster_SharedData) sharedData).renturnMovie (movieName,user.getUsername ());
        //---------------------------------------------------------------------------------------------------------
    
        if( ! broadcastMsg.substring (0,broadcastMsg.indexOf (' ')) .equals ( "movie"))
        {
            //  Error: user or movie doesn't exist, or user doesn't rent this movie
            connections.send (connectionId, errorRequestMessage ("return",broadcastMsg));
            return;
        }
        //  Ack: Movie has returned
        connections.send (connectionId, ackMessageSuccess ("return " + '"' + movieName + '"'));
        broadcast ( broadcastMsg );
    }
    //------------------------------------------------------------------------------------------------------------------
    //                                           (Admin) Request: Add Movie
    //------------------------------------------------------------------------------------------------------------------
    private void adminAddMovie (String message)
    {
        //- - - - - - - - - - - - - - - - - - - - - - -  Conditions Check - - - - - - - - - - - - - - - - - - - - - - -                             - Conditions Check -
        if (!isLoggedIn ())
        {
            //  Error: User is not logged in
            connections.send (connectionId, errorRequestMessage ("addmovie","User is not logged in"));
            return;
        }

        if (!user.getType ().equals ("admin"))
        {
            //  Error: User is not admin
            connections.send (connectionId, errorRequestMessage ("addmovie","User is not admin"));
            return;
        }
        
        int tokenIndx = message.indexOf ('"');
        
         // movie mame
        message = message.substring (tokenIndx+1);
            tokenIndx = message.indexOf ('"');
            
        String movieName = message.substring (0,tokenIndx);
        
        // amount
        message = message.substring (tokenIndx+2);
             tokenIndx =  message.indexOf (' ');
             
        String amount = message.substring (0,tokenIndx);
        
        
        // price
        String price = "";
        List<String> bannedCountries = new Vector<> ();
        message = message.substring (tokenIndx+1);
            tokenIndx =  message.indexOf (' ');
            // no banned countries
            if(tokenIndx == -1)
                 price = message;
            else
            {
                price = message.substring (0,tokenIndx);
                
                // bannedCountries
                message = message.substring (tokenIndx+1);
                    tokenIndx = message.indexOf ('"');
                    
                while (tokenIndx != -1)
                {
                    message = message.substring (tokenIndx + 1);
                    tokenIndx = message.indexOf ('"');
            
                    bannedCountries.add (message.substring (0, tokenIndx));
            
                    message = message.substring (tokenIndx + 1);
                    tokenIndx = message.indexOf ('"');
                }
        }
        
        //----------------------------------------Synchronization--------------------------------------------------
         String broadcastMsg = ((BlockBuster_SharedData) sharedData).addMovie (movieName,amount,price,bannedCountries);
        //---------------------------------------------------------------------------------------------------------
    
        if( ! broadcastMsg.substring (0,broadcastMsg.indexOf (' ')) .equals ( "movie"))
        {
            //  Error
            connections.send (connectionId, errorRequestMessage ("addmovie", broadcastMsg));
            return;
        }
        //  Ack: Movie Added
        connections.send (connectionId, ackMessageSuccess ("addmovie "  + '"' + movieName + '"'));
        broadcast ( broadcastMsg );
    }
    //------------------------------------------------------------------------------------------------------------------
    //                                           (Admin) Request: Remove Movie
    //------------------------------------------------------------------------------------------------------------------
    private void adminRemMovie (String message)
    {
        //- - - - - - - - - - - - - - - - - - - - - - -  Conditions Check - - - - - - - - - - - - - - - - - - - - - - -                                    - Conditions Check -
        if (!isLoggedIn ())
        {
            //  Error: User is not logged in
            connections.send (connectionId, errorRequestMessage ("remmovie","User is not logged in"));
            return;
        }

        if (!user.getType ().equals ("admin"))
        {
            //  Error: User is not admin
            connections.send (connectionId, errorRequestMessage ("remmovie","User is not admin"));
            return;
        }
    
        String movieName = message.substring (message.indexOf ('"')+1, message.lastIndexOf ('"'));
        
        //----------------------------------------Synchronization--------------------------------------------------
                String broadcastMsg = ((BlockBuster_SharedData) sharedData).removeMovie (movieName);
        //---------------------------------------------------------------------------------------------------------
        if( ! broadcastMsg.substring (0,broadcastMsg.indexOf (' ')) .equals ( "movie"))
        {
            //  Error: movie doesn't exist, or movie is currently rented
            connections.send (connectionId, errorRequestMessage ("remmovie",broadcastMsg));
            return;
        }
        //  Ack: Movie has removed
        connections.send (connectionId, ackMessageSuccess ("remmovie " + '"' + movieName + '"'));
        broadcast ( broadcastMsg );
    }
    //------------------------------------------------------------------------------------------------------------------
    //                                           (Admin) Request: Change Price
    //------------------------------------------------------------------------------------------------------------------
    private void adminChangePrice (String message)
    {
        //- - - - - - - - - - - - - - - - - - - - - - -  Conditions Check - - - - - - - - - - - - - - - - - - - - - - -
        if (!isLoggedIn ())
        {
            //  Error: User is not logged in
            connections.send (connectionId, errorRequestMessage ("changeprice","User is not logged in"));
            return;
        }
    
        if (!user.getType ().equals ("admin"))
        {
            //  Error: User is not admin
            connections.send (connectionId, errorRequestMessage ("changeprice","User is not admin"));
            return;
        }
    
        int tokenIndx = message.lastIndexOf ('"');
        
        String movieName = message.substring (message.indexOf ('"')+1, tokenIndx);
        String moviePrice =  message.substring (tokenIndx + 2); // assume input is correct
    
        //----------------------------------------Synchronization--------------------------------------------------
            String broadcastMsg = ((BlockBuster_SharedData) sharedData).changeMoviePrice (movieName,moviePrice);
        //---------------------------------------------------------------------------------------------------------
        if (broadcastMsg.equals (""))
        {
            //  Error: Movie doesn't exist
            connections.send (connectionId, errorRequestMessage ("changeprice","Movie doesn't exist"));
            return;
        }
        //  Ack: Movie's price has changed
        connections.send (connectionId, ackMessageSuccess ("changeprice " + '"' + movieName + '"'));
        broadcast ( broadcastMsg );
    }
    //------------------------------------------------------------------------------------------------------------------
    protected String errorRequestMessage (String msg, String reson) {return "ERROR request " + msg + " failed:" +
            " (" + reson + ")";}
    //------------------------------------------------------------------------------------------------------------------
    private String listToString(List<String> list)
    {
        String output =" ";
        for (String s : list)
            output += '"' + s + '"' + ' ';
        
        return output.substring (0,output.length ()-1);
    }
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
}

