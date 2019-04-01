package bgu.spl181.net.impl;

import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.BlockBuster_Protocol.BlockBuster_Movie;
import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.BlockBuster_Protocol.BlockBuster_User;
import bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.UserServiceText_User;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tools
{
    public static void intializeMaps(Map<String,BlockBuster_User> users, Map<String,BlockBuster_Movie> movies)
    {
        Gson gson = new GsonBuilder ().setPrettyPrinting().create();
        JsonParser parser = new JsonParser ();
        JsonObject jsonObjUsers = new JsonObject ();
        JsonObject jsonObjMovies = new JsonObject ();
        
        try
        {
            jsonObjMovies = (JsonObject)parser.parse
                    (new FileReader ("Database" + File.separator + "Movies.json"));
        }
        catch (FileNotFoundException e) {System.out.println ("File: Movies.json not found");}
        
        JsonArray array =  jsonObjMovies.getAsJsonArray ("movies");
        
        for(int i=0 ; i< array.size () ; i++)
        {
            BlockBuster_Movie movie = gson.fromJson (array.get (i), BlockBuster_Movie.class);
            movies.put (movie.getName (), movie);
        }
        
        
        
        try
        {
            jsonObjUsers = (JsonObject)parser.parse
                    (new FileReader ("Database" + File.separator + "Users.json"));
        }
        catch (FileNotFoundException e) {System.out.println ("File: Users.json not found");}
        
        array =  jsonObjUsers.getAsJsonArray ("users");
        
        for(int i=0 ; i< array.size () ; i++)
        {
            BlockBuster_User user = gson.fromJson (array.get (i), BlockBuster_User.class);
            users.put (user.getUsername (), user);
        }
    }
    //------------------------------------------------------------------------------------------------------------------
    public static void writeToMovies(Map<String,BlockBuster_Movie> movies) {
        JsonWriter jsonOut;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try
        {
            jsonOut = new JsonWriter (new FileWriter ("Database" + File.separator + "Movies.json"));
            
            jsonOut.beginObject().name ("movies");
            jsonOut.beginArray ();
            List<BlockBuster_Movie> list = new ArrayList<> ();
            
            for(  Map.Entry<String, BlockBuster_Movie> entry :movies.entrySet ())
            {
                list.add (entry.getValue ());
            }
            
            for( BlockBuster_Movie movie : list) gson.toJson (movie , BlockBuster_Movie.class , jsonOut);
            
            jsonOut.endArray ();
            jsonOut.endObject ();
            jsonOut.flush ();
        }
        catch (FileNotFoundException e){System.out.println ("File: Users.json not found");}
        catch (IOException e){}
    }
    //------------------------------------------------------------------------------------------------------------------
    public static void writeUsers(Map<String,UserServiceText_User> users)
    {
        JsonWriter jsonOut;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try
        {
            jsonOut = new JsonWriter (new FileWriter ("Database" + File.separator + "Users.json"));
            
            jsonOut.beginObject().name ("users");
            jsonOut.beginArray ();
            List<UserServiceText_User> list = new ArrayList<> ();
            
            for(  Map.Entry<String, UserServiceText_User> entry :users.entrySet ())
            {
                list.add (entry.getValue ());
            }
            
            for( UserServiceText_User user : list) gson.toJson (user , BlockBuster_User.class , jsonOut);
            
            jsonOut.endArray ();
            jsonOut.endObject ();
            jsonOut.flush ();
        }
        catch (FileNotFoundException e){System.out.println ("File: Users.json not found");}
        catch (IOException e){}
    }
    
    
}


