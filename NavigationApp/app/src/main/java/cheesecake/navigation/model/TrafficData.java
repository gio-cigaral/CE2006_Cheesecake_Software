package cheesecake.navigation.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Gio 26/03/20
 *
 * Stores all Road value objects
 *
 * TODO:
 *  - (OPTIONAL - Possibly for performance improvement) Create subset of this with viewable items
 */
public class TrafficData {

    @SerializedName("odata.metadata")
    private String metadata;

    @SerializedName("value")
    private ArrayList<Road> value;

    private ArrayList<Road> displayValues;

    public void createDisplayItems() {
        displayValues = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            displayValues.add(value.get(i));
        }
    }
    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public ArrayList<Road> getValue() {
        return value;
    }

    public void setValue(ArrayList<Road> value) {
        this.value = value;
    }

    public ArrayList<Road> getDisplayValues() {
        return displayValues;
    }

    public void setDisplayValues(ArrayList<Road> displayValues) {
        this.displayValues = displayValues;
    }
}
