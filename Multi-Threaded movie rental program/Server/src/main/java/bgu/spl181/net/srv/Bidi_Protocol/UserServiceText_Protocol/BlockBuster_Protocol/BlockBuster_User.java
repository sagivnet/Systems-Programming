package bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.BlockBuster_Protocol;

import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.UserServiceText_User;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class BlockBuster_User extends UserServiceText_User
{
    //------------------------------------------------------------------------------------------------------------------
    //                                                  BlockBuster User
    //------------------------------------------------------------------------------------------------------------------
    /**                                                     Fields                                                    */
    //------------------------------------------------------------------------------------------------------------------
    protected final String country;
    protected final List<BlockBuster_MovieForUser> movies;
    protected String balance;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                     Methods                                                   */
    //------------------------------------------------------------------------------------------------------------------
    public BlockBuster_User (String username, String password, String type, String country)
    {
        super (username, password, type);
        this.country = country;
        movies = new Vector<> ();
        balance = "0";
    }
    //------------------------------------------------------------------------------------------------------------------
    public Integer addToBalance (Integer toAdd)
        {
            Integer newBalance = Integer.parseInt (balance) + toAdd;
            balance = newBalance.toString ();
            return Integer.parseInt (balance);
        }
    //------------------------------------------------------------------------------------------------------------------
    public Integer decFromBalance (Integer toDec)
    {
        Integer newBalance = Integer.parseInt (balance) - toDec;
        balance = newBalance.toString ();
        return Integer.parseInt (balance);
    }
    //------------------------------------------------------------------------------------------------------------------
    public void addMovie (BlockBuster_MovieForUser movie)
        { movies.add (movie); }
    //------------------------------------------------------------------------------------------------------------------
    public void removeMovie (String movieName)
    {
        for(BlockBuster_MovieForUser movie : movies)
            if (movie.getName ().equals (movieName))
            {
                movies.remove (movie);
                return;
            }
    }
    //------------------------------------------------------------------------------------------------------------------
    public Integer getBalance() { return Integer.parseInt (balance); }
    //------------------------------------------------------------------------------------------------------------------
    public boolean checkIfRent (String movieName)
    {
        for(BlockBuster_MovieForUser movie : movies)
            if (movie.getName ().equals (movieName))
                return true;
        return false;
        
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMovies ()
    {
        ArrayList<String> moviesNames = new ArrayList<> ();
        for (BlockBuster_MovieForUser movie: movies)
            moviesNames.add (movie.getName ());
        
        return moviesNames.toString ();
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getInfo ()
    {
        String ret =  "UserName: "+ username + '\n' +
                "Type: " + type + '\n' +
                "Country: " + country + '\n' +
                "Balance: " + balance + '\n' +
                "Movies: " + getMovies ().toString ();
        return ret;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getCountry ()
    {
        return country;
    }
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
}
