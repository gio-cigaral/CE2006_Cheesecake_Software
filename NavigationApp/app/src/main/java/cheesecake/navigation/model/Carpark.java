package cheesecake.navigation.model;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

/**
 * Created by Gio 23/03/20
 *
 * Carpark class to store information from DataMall Carpark Availability API
 *
 * TODO: create method to split location field into latitude & longitude (double) fields
 */
public class Carpark implements Comparable<Carpark> {

    //Carpark data fields
    @SerializedName("CarParkID")
    private String carparkID;

    @SerializedName("Area")
    private String area;

    @SerializedName("Development")
    private String development;

    @SerializedName("Location")
    private String location;

    @SerializedName("AvailableLots")
    private int availableLots;

    @SerializedName("LotType")
    private String lotType;

    @SerializedName("Agency")
    private String agency;

    private double latitude;
    private double longitude;

    private Location startLocation;

    //Distance to user's current location from carpark
    private float distanceTo;

    @NotNull
    @Override
    public String toString() {
        return "Carpark ID - " + this.carparkID + " || Available lots - " + this.availableLots;
    }

    @Override
    public int compareTo(Carpark o) {
        return Float.compare(distanceTo, o.getDistanceTo());
    }

    public void calcDistance(Location targetLocation) {
        if (location.equals("")) { return; }
        parseLocation();
        distanceTo = targetLocation.distanceTo(startLocation);
    }

    public void parseLocation() {
        if (location.equals("")) { return; }
        Scanner sc = new Scanner(location);

        latitude = sc.nextDouble();
        longitude = sc.nextDouble();

        startLocation = new Location("");
        startLocation.setLatitude(latitude);
        startLocation.setLongitude(longitude);

        sc.close();
    }

    public String getCarparkID() {
        return carparkID;
    }

    public void setCarparkID(String carparkID) {
        this.carparkID = carparkID;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDevelopment() {
        return development;
    }

    public void setDevelopment(String development) {
        this.development = development;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAvailableLots() {
        return availableLots;
    }

    public void setAvailableLots(int availableLots) {
        this.availableLots = availableLots;
    }

    public String getLotType() {
        return lotType;
    }

    public void setLotType(String lotType) {
        this.lotType = lotType;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public float getDistanceTo() {
        return distanceTo;
    }

    public void setDistanceTo(float distanceTo) {
        this.distanceTo = distanceTo;
    }
}
