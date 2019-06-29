package com.example.carsmap;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String TAG = "MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkForContactsPermissions();
    }

    private void StartMap()
    {
        Log.d(TAG,"StartMap: was called");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG,"onMapReady: was called");
        Toast.makeText(this, "Map is working", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        LatLng Justai = new LatLng(54.719721,25.213366);
        mMap.addMarker(new MarkerOptions().position(Justai).title("Namai"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Justai));

    }

    /**
    Permissions
     */
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION };
    private static final int REQUEST_CONTACTS = 1;

    private void checkForContactsPermissions() {
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
