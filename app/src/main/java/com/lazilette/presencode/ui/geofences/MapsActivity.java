package com.lazilette.presencode.ui.geofences;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.lazilette.presencode.R;
import com.lazilette.presencode.databinding.ActivityMapsBinding;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private float geo_radius = 30;
    private String GEOFENCE_ID = "0609";
    private LatLng pinBKPSDM = new LatLng(-7.265680, 110.398875);
    //bkpsdm di -7.324105, 110.505515
    private GeofenceHelper geofenceHelper;
    private GeofencingClient geofencingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        addGeofence();
        mMap.addMarker(new MarkerOptions().position(pinBKPSDM).title("Marker in BKPSDM"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pinBKPSDM));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pinBKPSDM, 17), 3000, null);
        //circling
        Log.d("mylog", "onSuccess: map ready");
        Toast.makeText(MapsActivity.this, "map ready", Toast.LENGTH_SHORT).show();
        addCircle();
        trackUserLocation();


    }

    public void trackUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    public void addGeofence() {
        Toast.makeText(MapsActivity.this, "Geofence triggering on process", Toast.LENGTH_SHORT).show();
        Log.d("mylog", "onSuccess: Geofence triggering added");
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, pinBKPSDM, geo_radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "onSuccess: Geofence added");
                            Toast.makeText(MapsActivity.this, "geofence added", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: fail");
                            String errorMsg = geofenceHelper.getErrorMessage(e);
                            Toast.makeText(MapsActivity.this, "err.."+errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    });



        }







    public void addCircle(){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(pinBKPSDM);
        circleOptions.radius(geo_radius);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
        Toast.makeText(MapsActivity.this, "circle ready", Toast.LENGTH_SHORT).show();
        trackUserLocation();

    }
}