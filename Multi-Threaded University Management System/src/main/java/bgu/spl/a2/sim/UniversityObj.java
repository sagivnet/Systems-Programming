package bgu.spl.a2.sim;


import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;
public class UniversityObj
{
    Integer threads;
    Computer[] Computers;
    @SerializedName("Phase 1")
    ArrayList<ActionObj> Phase1;
    @SerializedName("Phase 2")
    ArrayList<ActionObj> Phase2;
    @SerializedName("Phase 3")
    ArrayList<ActionObj> Phase3;
}
