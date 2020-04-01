package cheesecake.navigation.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import cheesecake.navigation.model.Carpark;
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

    private static GoogleMap mMap;

    private static Location lastCurrentLocation;

    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;

    Location mLastLocation;

    private static String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        caller = getIntent().getStringExtra("caller");
        if (caller.equals("Directions")) {
            lastCurrentLocation = getIntent().getParcelableExtra("Location");
        }
        else if (caller.equals("Main")) {
            lastCurrentLocation = mLastLocation;
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int reqCode = 1;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Location Permission already granted
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        } else {
            //Request Location Permission
            checkLocationPermission();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, reqCode);
            return;
        }

        mMap.setMyLocationEnabled(true);

        ParserTask parserCarpark = new ParserTask();
        parserCarpark.execute("carpark", "http://datamall2.mytransport.sg/ltaodataservice/CarParkAvailabilityv2");

        new ParserTask().execute("traffic", "http://datamall2.mytransport.sg/ltaodataservice/TrafficSpeedBandsv2");

        if (caller.equals("Directions")) {
            LatLng latLong = new LatLng(lastCurrentLocation.getLatitude(), lastCurrentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLong);
            markerOptions.title("Destination");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end));
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 16));
        }

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


            // carpark markers
            int i;
            for(i=0; i<5; i++) {
                Carpark temp = cparkData.getDisplayValues().get(i);
                Location tempLocation = temp.getStartLocation();

                LatLng latlong = new LatLng(tempLocation.getLatitude(), tempLocation.getLongitude());
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(latlong);
                markerOption.title(temp.getDevelopment());
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_carpark));
                mMap.addMarker(markerOption);

                //move map camera to nearest carpark
                if(i==0){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));
                }
            }

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

            Log.d("Async - Parser", ds + "Parse finished");
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;

                if (!caller.equals("Directions")) {
                    lastCurrentLocation = location;
                }

                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SummaryActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

}
