package cheesecake.navigation.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import cheesecake.navigation.model.CarparkData;
import cheesecake.navigation.model.TrafficData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anton 22/03/20
 *  - Modified by Gio 22/03/20
 *
 *  TODO: create and update UI elements with data from APIs (note - will probably have to use onPostExecute() method inside of AsyncTask)
 */
public class SummaryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static CarparkData cparkData = new CarparkData();
    private static TrafficData trafficData = new TrafficData();

    private static RecyclerView carparkRecyclerView;
    private static RecyclerView.Adapter carparkAdapter;
    private static RecyclerView.LayoutManager carparkManager;

    private static RecyclerView trafficRecyclerView;
    private static RecyclerView.Adapter trafficAdapter;
    private static RecyclerView.LayoutManager trafficManager;

    private FusedLocationProviderClient fusedLocationClient;
    private static Location lastCurrentLocation;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        Task test = fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        // Got last known location. In some rare situations this can be null.
//                        if (location != null) {
//                            // Logic to handle location object
//                            targetLocation = location;
//                        } else {
//                            // Set test location to Plaza Singapura mall
//                            targetLocation = new Location("");
//                            targetLocation.setLatitude(1.30015359833);
//                            targetLocation.setLongitude(103.844704628);
//                        }
//                    }
//                });
//
//        if (Objects.isNull(targetLocation)) {
//            Log.d("Create", "FAILED to create targetlocation");
//        } else {
//            Log.d("Create", "Target location lat: " + targetLocation.getLatitude());
//            Log.d("Create", "Target location longitude: " + targetLocation.getLongitude());
//        }

        lastCurrentLocation = new Location("");
        lastCurrentLocation.setLatitude(1.30015359833);
        lastCurrentLocation.setLongitude(103.844704628);

        Log.d("Create", "Layout created");

        new ParserTask().execute("carpark", "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2");
        new ParserTask().execute("traffic", "http://datamall2.mytransport.sg/ltaodataservice/TrafficSpeedBandsv2");

        //Create (Scroll list) Recycler View for Carpark data
        carparkRecyclerView = findViewById(R.id.carparkRecyclerView);
        //Remove if having errors with layout sizes etc.
        carparkRecyclerView.setHasFixedSize(true);

        //Set Linear Layout Manager to Carpark Recycler View
        carparkManager = new LinearLayoutManager(this);
        carparkRecyclerView.setLayoutManager(carparkManager);

        //Create (Scroll list) Recycler View for Traffic data
        trafficRecyclerView = findViewById(R.id.trafficRecyclerView);
        //Remove if having errors with layout sizes etc.
        trafficRecyclerView.setHasFixedSize(true);

        //Set Linear Layout Manager to Traffic Recycler View
        trafficManager = new LinearLayoutManager(this);
        trafficRecyclerView.setLayoutManager(trafficManager);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int reqCode = 1;

        LatLng currentPlace = new LatLng(lastCurrentLocation.getLatitude(),lastCurrentLocation.getLongitude());

        // LatLng current = new LatLng(10.762963, 106.682394);

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current))
                .title("My Location")
                .position(currentPlace));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, reqCode);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    /**
     * Run Parser as an AsyncTask (new background thread)
     */
    private static class ParserTask extends AsyncTask<String, Void, String> {

        /**
         * Connect to DataMall API
         * @param strings - arguments list [dataset, api url]
         * @return null
         */
        @Override
        protected String doInBackground(@NotNull String... strings) {
            Log.d("Async - ParserTask", "AsyncTask running");

            String dataset = strings[0];
            String url = strings[1];
            String response = "";

            try {
                response = connect(url);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Async - ParserTask", "URL connection failed");
            }

            parseResponse(dataset, response);

            Log.d("Async - ParserTask", "AsyncTask finished");

            return dataset;
        }

        @Override
        protected void onPostExecute(String ds) {
            super.onPostExecute(ds);

            //Set Adapter to Carpark Recycler View
            if (ds.equals("carpark")){
                carparkAdapter = new CarparkAdapter(cparkData);
                carparkRecyclerView.setAdapter(carparkAdapter);
                Log.d("Async - Post", "Carpark UI Adapter Item Count: " + carparkAdapter.getItemCount());
            }
            else if (ds.equals("traffic")){
                trafficAdapter = new TrafficAdapter(trafficData);
                trafficRecyclerView.setAdapter(trafficAdapter);
                Log.d("Async - Post", "Traffic UI Adapter Item Count: " + trafficAdapter.getItemCount());
            }

            Log.d("Async - Post", "Data UI Adapter Refreshed");
        }

        /**
         * Connect to DataMall API
         * @throws Exception - connection error
         */
        private String connect(String datasetURL) throws Exception {
            Log.d("Async - Connect", "Connect function running");

            //Create HTTP Client
            OkHttpClient client = new OkHttpClient();

            //Create HTTP request
            Request request = new Request.Builder()
                    .header("AccountKey", "35W+luamQVWHuy48F44jDw==")
                    .url(datasetURL)
                    .build();

            //Send HTTP request and receive JSON String response
            Response response = client.newCall(request).execute();
            String resp = Objects.requireNonNull(response.body()).string();

            Log.d("Async - Connect", "Response:" + resp);

            return resp;
        }

        /**
         * Parse JSON API response into relevant dataset
         * @param ds - dataset to parse
         * @param response - JSON String response from DataMall API
         */
        private void parseResponse(String ds, String response) {
            Log.d("Async - Parser", ds + " Parse running");

            //Create parser
            Gson gson = new Gson();

            //Log.d("Async - Parser", "Traffic data: " + Objects.isNull(trafficData));

            //Parse into dataset object
            if (ds.equals("carpark")){
                cparkData = gson.fromJson(response, CarparkData.class);
                cparkData.createDisplayItems(lastCurrentLocation);
            } else if (ds.equals("traffic")){
                trafficData = gson.fromJson(response, TrafficData.class);
                trafficData.createDisplayItems();
            }

            //Log.d("Async - Parser", "Traffic data: " + Objects.isNull(trafficData));

            /* LOG TESTING
            if (ds.equals("carpark")) {
                Log.d("Async - Parser - TEST", "Carpark metadata: " + cparkData.getMetadata());
                Log.d("Async - Parser - TEST", "Carpark num values: " + cparkData.getValue().size());
                Log.d("Async - Parser - TEST", "Carpark test value:" + cparkData.getValue().get(0).toString());
            }

            if (ds.equals("traffic")) {
                Log.d("Async - Parser - TEST", "Traffic metadata: " + trafficData.getMetadata());
                Log.d("Async - Parser - TEST", "Traffic num values: " + trafficData.getValue().size());
                Log.d("Async - Parser - TEST", "Traffic test value:" + trafficData.getValue().get(0).toString());
            }
             */

            Log.d("Async - Parser", ds + "Parse finished");
        }
    }
}
