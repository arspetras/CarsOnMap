package com.example.carsmap;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.nfc.Tag;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String TAG = "MESSAGE";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean permissionGranted = false;
    private static float ZOOM = 15f;
    private Boolean focusOnUser = true;

    String listLength;
    public String[] plateNumber;
    public String[] latitude;
    public String[] longitude;
    public String[] address;
    public String[] title;
    public String[] photoUrl;
    public String[] distanceToUser;
    private ClusterManager mClusterManager;
    private ClusterRenderer mClusterRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    public double lat;
    public double lon;
    public String carId = "-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForContactsPermissions();

        plateNumber = getIntent().getStringArrayExtra("1");
        latitude = getIntent().getStringArrayExtra("2");
        longitude = getIntent().getStringArrayExtra("3");
        address = getIntent().getStringArrayExtra("4");
        title = getIntent().getStringArrayExtra("5");
        photoUrl = getIntent().getStringArrayExtra("6");
        listLength = getIntent().getStringExtra("7");
        carId = getIntent().getStringExtra("8");
        distanceToUser = new String[100];

    }


    private void StartMap() {
        Log.d(TAG, "StartMap: was called");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady: was called");
        Toast.makeText(this, "Map is working", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        if(permissionGranted)
        {
            GetCurrentLocation();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mMap.setMyLocationEnabled(true);
        addCarMarkers();
        // calculate distance to user
        for(int x = 0;x< Integer.parseInt(listLength);x++) {
            distanceToUser[x] = String.valueOf(GetDistance(lat, lon, Double.parseDouble(latitude[x]), Double.parseDouble(longitude[x])));
        }

    }

    private void GetCurrentLocation()
    {
        Log.d(TAG,"GetCurrentLocation: getting current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(permissionGranted)
            {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG,"onComplete: found");
                            Location currentLocation = (Location) task.getResult();
                            lat = currentLocation.getLatitude();
                            lon = currentLocation.getLongitude();
                            LatLng ll = new LatLng(lat,lon);
                            if(focusOnUser && carId.equals("-1")) moveCameraView(ll,ZOOM);
                            else{
                                ll = new LatLng(Double.parseDouble(latitude[Integer.parseInt(carId)]),Double.parseDouble(longitude[Integer.parseInt(carId)]));
                                moveCameraView(ll,ZOOM);

                            }
                        }
                        else
                        {
                            Log.d(TAG,"onComplete: Location not found");
                            Toast.makeText(MainActivity.this, "Unable to detect current location", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }catch (SecurityException e)
        {
            Log.e(TAG, "GetCurrentLocation: Security Exception: " + e.getMessage());
        }
    }


    /**
        Method to move map to certain point (move camera)
     */

    private void moveCameraView(LatLng latLng, float zoom)
    {
        Log.d(TAG,"moveCameraView: Zooming to: " + latLng.latitude + ": " + latLng.longitude );

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    /**
        Method to show list of cars
     */
    public void showCarsList(View view)
    {
        Intent intent = new Intent(this, Cars.class);
        intent.putExtra("distanceToUser", distanceToUser);
        startActivity(intent);
    }

    //test
    public void showMyCar()
    {
        LatLng ll = new LatLng(Double.parseDouble(latitude[0]),Double.parseDouble(longitude[0]));
        moveCameraView(ll,20f);
    }

    private void addCarMarkers()
    {
        try {
            if (mMap != null) {
                if (mClusterManager == null) {
                    mClusterManager = new ClusterManager<ClusterMarker>(this.getApplicationContext(), mMap);
                }
                if (mClusterRenderer == null) {
                    mClusterRenderer = new ClusterRenderer(this, mMap, mClusterManager);
                    mClusterManager.setRenderer(mClusterRenderer);
                }
                for (int x = 0; x < Integer.parseInt(listLength) ; x++) {
                    String snippet = "Plate Number: " + plateNumber[x];

                    int avatar = R.drawable.img;

                    ClusterMarker newClusterMarker = new ClusterMarker(
                            new LatLng(Double.parseDouble(latitude[x]), Double.parseDouble(longitude[x])),
                            title[x],
                            snippet,
                            avatar
                    );

                    mClusterManager.addItem(newClusterMarker);
                    mClusterMarkers.add(newClusterMarker);

                }
                mClusterManager.cluster();

            }
        }catch (NullPointerException e){
            Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
        }
    }


    /**
     Method to calculate distance between car and user
     Using Haversine method
     */
    private static double GetDistance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            dist = Math.ceil(dist);
            dist = dist /1000;
            return (dist);
        }
    }






    /**
    Permissions
     */
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION };
    private static final int REQUEST_CONTACTS = 1;

    private void checkForContactsPermissions() {
        permissionGranted = false;
        // Check if all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission not granted.
            Log.i(TAG, "Location permissions have NOT been granted. Requesting permissions.");
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        } else {
            // Permissions have been granted.
            Log.i(TAG, "Location permissions have already been granted. MAP is ready.");
            permissionGranted = true;
            StartMap();
        }

    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACTS) {
            Log.i(TAG, "Received response for contact permissions request.");

            if (verifyPermissions(grantResults)) {
                Log.i(TAG, "GRANTED");
                permissionGranted = true;
                StartMap();
            } else {
                Log.i(TAG, "DENIED");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private  boolean verifyPermissions(int[] grantResults) {
        if(grantResults.length < 1){
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

}
