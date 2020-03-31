package cheesecake.navigation.model;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Gio 26/03/20
 *
 * Stores all Carpark value objects
 *
 * following data structure suggestion from https://stackoverflow.com/questions/31734264/converting-jsonobject-to-object-list
 * to convert JSON object to Java Objects - Carpark
 *
 * Note - if required might be able to make serializable to store data locally
 *
 * TODO:
 *  - Collect data in this class when declaring or as methods called in constructor
 *      - national total & available lots
 *      - area total & available lots (include list field of areas)
 *  - (OPTIONAL - Possibly for performance improvement) Create subset of this with viewable items
 */
public class CarparkData {

    @SerializedName("odata.metadata")
    private String metadata;

    @SerializedName("value")
    private ArrayList<Carpark> value;

    private ArrayList<Carpark> displayValues;

    public void createDisplayItems(Location targetLocation) {
        removeItems();
        calcDistances(targetLocation);
        sortVals();

        displayValues = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            displayValues.add(value.get(i));
        }
    }

    public void calcDistances(Location targetLocation) {
        for (Carpark carpark : value) {
            carpark.calcDistance(targetLocation);
        }
    }

    public void sortVals() {
        Collections.sort(value);
    }

    public void removeItems() {
        ArrayList<Carpark> temp = new ArrayList<>();
        for (Carpark carpark : value) {
            String location = carpark.getLocation();

            if (location.equals("")) {
                temp.add(carpark);
            }
        }

        value.removeAll(temp);
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public ArrayList<Carpark> getValue() {
        return value;
    }

    public void setValue(ArrayList<Carpark> value) {
        this.value = value;
    }

    public ArrayList<Carpark> getDisplayValues() {
        return displayValues;
    }

    public void setDisplayValues(ArrayList<Carpark> displayValues) {
        this.displayValues = displayValues;
    }
}
