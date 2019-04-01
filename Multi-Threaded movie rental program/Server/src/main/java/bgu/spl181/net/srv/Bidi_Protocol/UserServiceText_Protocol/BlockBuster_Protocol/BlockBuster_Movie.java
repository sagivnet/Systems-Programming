package bgu.spl181.net.srv.Bidi_Protocol.UserServiceText_Protocol.BlockBuster_Protocol;

import java.util.List;

public class BlockBuster_Movie extends BlockBuster_MovieForUser
{
    //------------------------------------------------------------------------------------------------------------------
    //                                                BlockBuster Movie
    //------------------------------------------------------------------------------------------------------------------
    /**                                                     Fields                                                    */
    //------------------------------------------------------------------------------------------------------------------
//    protected String id;
//    protected String name;
    protected String price;
    protected List<String> bannedCountries;
    protected String availableAmount;
    protected String totalAmount;
    //------------------------------------------------------------------------------------------------------------------
    /**                                                      Methods                                                  */
    //------------------------------------------------------------------------------------------------------------------
    
    public BlockBuster_Movie (Integer id ,String name, String price,
                              String totalAmount,
                              List<String> bannedCountries)
    {
        this.name = name;
        this.price = price;
        this.bannedCountries = bannedCountries;
        this.totalAmount = totalAmount;
        this.availableAmount = totalAmount;
        this.id = id.toString ();
    }
    //------------------------------------------------------------------------------------------------------------------
    public Integer getId () { return Integer.parseInt (id); }
    //------------------------------------------------------------------------------------------------------------------
    public void setId (String id) {
        this.id = id;
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getName () {
        return name;
    }
    //------------------------------------------------------------------------------------------------------------------
    public Integer getPrice () {
        return Integer.parseInt (price);
    }
    //------------------------------------------------------------------------------------------------------------------
    public void setPrice (Integer newPrice) {
        this.price = newPrice.toString ();
    }
    //------------------------------------------------------------------------------------------------------------------
    public List<String> getBannedCountries () { return bannedCountries; }
    //------------------------------------------------------------------------------------------------------------------
    public Integer getAvailableAmount () { return Integer.parseInt (availableAmount); }
    //------------------------------------------------------------------------------------------------------------------
    public void returnMovie ()
    {
        Integer available = Integer.parseInt (availableAmount);
        available++;
        availableAmount = available.toString ();
    }
    //------------------------------------------------------------------------------------------------------------------
    public boolean rentMovie ()
    {
        Integer available = Integer.parseInt (availableAmount);
        if(available == 0)
            return false;
        
        available--;
        availableAmount = available.toString ();
        return true;
    }
    //------------------------------------------------------------------------------------------------------------------
    public boolean isRemovable()
    {
        return availableAmount.equals (totalAmount);
    }
    //------------------------------------------------------------------------------------------------------------------
    public String getToatlAmount () { return totalAmount; }
    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
}
