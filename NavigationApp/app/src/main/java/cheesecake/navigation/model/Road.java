package cheesecake.navigation.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Scanner;

/**
 * Created by Gio 26/03/20
 *
 * Road class to store information from DataMall Traffic Speed Bands API
 *
 * TODO: (OPTIONAL) find typical max speed limit for each type of road to gauge traffic level
 * TODO: create method to split location field into latitude & longitude (double) fields (note - location field has two sets of coordinates for start & end point of the road)
 */
public class Road {

    @SerializedName("LinkID")
    private String linkID;

    @SerializedName("RoadName")
    private String roadName;

    @SerializedName("RoadCategory")
    private String roadCategory;

    @SerializedName("SpeedBand")
    private int speedBand;

    @SerializedName("MinimumSpeed")
    private String minSpeed;

    @SerializedName("MaximumSpeed")
    private String maxSpeed;

    @SerializedName("Location")
    private String location;

    @NonNull
    @Override
    public String toString() {
        return "LinkID - " + this.linkID + " Road Name - " + this.roadName + " || Speed Band - " + this.speedBand;
    }

    public String getLinkID() {
        return linkID;
    }

    public void setLinkID(String linkID) {
        this.linkID = linkID;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getRoadCategory() {
        return roadCategory;
    }

    public void setRoadCategory(String roadCategory) {
        this.roadCategory = roadCategory;
    }

    public int getSpeedBand() {
        return speedBand;
    }

    public void setSpeedBand(int speedBand) {
        this.speedBand = speedBand;
    }

    public String getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(String minSpeed) {
        this.minSpeed = minSpeed;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

//    public void ConversionOfLoc(){
//        Scanner myObj = new Scanner(System.in);
//        double lattitudeOfStart = Double.parseDouble(myObj.next());
//        double longitudeOfStart = Double.parseDouble(myObj.next());
//        double lattitudeOfEnd = Double.parseDouble(myObj.next());
//        double longitudeOfEnd = Double.parseDouble(myObj.next());
//    }

}
