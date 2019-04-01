package bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.BlockBuster_Protocol;

import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.UserServiceText_SharedData;
import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.UserServiceText_User;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BlockBuster_SharedData extends UserServiceText_SharedData
{
    //------------------------------------------------------------------------------------------------------------------
    //                                             UserServiceText SharedData
    //------------------------------------------------------------------------------------------------------------------
    /**                                                     Fields                                                    */
    //------------------------------------------------------------------------------------------------------------------
    protected Map<String,BlockBuster_Movie> movies;
    protected Runnable writeMovies;
    protected Integer maxID = -1;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                     Methods                                                   */
    //------------------------------------------------------------------------------------------------------------------
    public BlockBuster_SharedData (Map<String,UserServiceText_User> users,
                                   Runnable writeUsers,
                                   Map<String,BlockBuster_Movie> movies,
                                   Runnable writeMovies)
    {
        super(users,writeUsers);
        
       // this.users =map;
        this.movies = movies;
        this.writeMovies = writeMovies;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String addMovie(String movieName, String amount, String price, List<String> bannedCountries)
    {
        // Conditions check
        if (movies.containsKey (movieName))
            return "Movie is already exist";
        
        if (Integer.parseInt (amount) <= 0)
            return "Amount must be > 0";
        
        if (Integer.parseInt (price) <= 0)
            return "Price must be > 0";
        
        //  first movie that is not read from json
        if(maxID == -1)
            for( Map.Entry<String, BlockBuster_Movie> entry :movies.entrySet ())
                if(entry.getValue ().getId () > maxID)
                    maxID = entry.getValue().getId ();
        
        // allocate new movie
        BlockBuster_Movie movie = new BlockBuster_Movie (maxID+1,movieName,price,amount,bannedCountries);
        
        // update data structure
        
        synchronized (movies)
        {
            movies.put (movie.getName (),movie);
            writeMovies.run ();
            
            return movieToString (movie);
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public String removeMovie(String movieName)
    {
        // Conditions check
        if (!movies.containsKey (movieName))
            return "Movie is not exist";
        
        BlockBuster_Movie movie = movies.get (movieName);
        synchronized (movie)
        {
            if (!movie.isRemovable ())
                return "Movie is currently rented";
            
                movies.remove (movie.getName ());
                writeMovies.run ();
                
                return "movie "+'"'+ movieName +'"'+ " removed";
            
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public List<String> getMoviesNames()
    {
        return new ArrayList (movies.keySet ());
    }
    //------------------------------------------------------------------------------------------------------------------
    public Integer getMoviePrice(String movieName)
    {
        BlockBuster_Movie movie = movies.get (movieName);
        synchronized (movie)
        { return movie.getPrice (); }
    }
    //------------------------------------------------------------------------------------------------------------------
    public String rentMovie(String movieName, String userName)
    {
        BlockBuster_User user = (BlockBuster_User) users.get (userName);
        if (user != null) synchronized (user)
        {
            if (!user.checkIfRent (movieName))
            {
                BlockBuster_Movie movie = movies.get (movieName);
                
                if (movie != null) synchronized (movie)
                {
                    if(!movie.getBannedCountries ().contains (user.getCountry ()))
                    {
                        if(user.getBalance () >= movie.getPrice ())
                        {
                            if (movie.rentMovie ())
                            {
                                user.addMovie (movie);
                                user.decFromBalance (movie.getPrice ());
            
                                writeMovies.run ();
                                writeUsers.run ();
            
                                return movieToString (movie);
                            }
                            else
                                return "No copies left";
                        }
                        else
                            return "User doesn't have enough money";
                    }
                    else
                        return "Movie is not available in user's country";
                }
                else
                    return "No such movie";
            }
            else
                return "User already rent this movie";
        }
        else
            return "User does't exist";
    }
    //------------------------------------------------------------------------------------------------------------------
    public String renturnMovie(String movieName, String userName)
    {
        BlockBuster_User user = (BlockBuster_User) users.get (userName);
        if (user != null)  synchronized (user)
        {
            // user does't rent this movie
            if (user.checkIfRent (movieName))
            {
                BlockBuster_Movie movie = movies.get (movieName);
                
                if (movie != null) synchronized (movie)
                {
                    movie.returnMovie ();
                    user.removeMovie (movieName);
                    
                    writeMovies.run ();
                    writeUsers .run ();
                    
                    return movieToString (movie);
                }
                return "No such movie";
            }
            return "User does't rent this movie";
        }
        return "User does't exist";
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getMovieInfo(String movieName)
    {
        String info = "";
        
        //  Conditions check
        if (movies.containsKey (movieName))
        {
            BlockBuster_Movie movie = movies.get (movieName);
            synchronized (movie)
            {
                info = '"' + movieName + '"' + " " + movie.getAvailableAmount () + " " +
                        movie.getPrice ()+ " " + listToString (movie.getBannedCountries ());
            }
        }
        return info;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String addToUserBalance(String toAdd, String userName)
    {
        int amount = Integer.parseInt (toAdd);
        if (amount < 0)
            return "Negative amount";
        
        BlockBuster_User user = (BlockBuster_User) users.get (userName);
        if(user != null )synchronized (user)
        {
            Integer toReturn = user.addToBalance (amount);
            
            writeUsers.run ();
            
            return toReturn.toString ();
        }
        return "User is not exist";
    }
    //------------------------------------------------------------------------------------------------------------------
    public String changeMoviePrice (String movieName, String moviePrice)
    {
        int price = Integer.parseInt (moviePrice);
        BlockBuster_Movie movie = movies.get (movieName);
        
        if (movie != null) synchronized (movie)
        {
            movie.setPrice (price);
            writeMovies.run ();
            
            return movieToString(movie);
        }
        return "";
    }
    //------------------------------------------------------------------------------------------------------------------
    private String listToString(List<String> list)
    {
        String output =" ";
        for (String s : list)
            output += '"' + s + '"' + ' ';
        
        return output.substring (0,output.length ()-1);
    }
    //------------------------------------------------------------------------------------------------------------------
    private String movieToString(BlockBuster_Movie movie) // without banned countries
    {return "movie " + '"' + movie.getName () + '"'+ " " +movie.getAvailableAmount () + " " + movie.getPrice ();}
    
    
    public void addUser (BlockBuster_User user)
    {
        synchronized (users)
        {
            users.put (user.getUsername (), user);
            writeUsers.run ();
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public boolean removeUser (BlockBuster_User user)
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
    //------------------------------------------------------------------------------------------------------------------
}
