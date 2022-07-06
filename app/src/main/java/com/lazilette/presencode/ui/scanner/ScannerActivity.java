package com.lazilette.presencode.ui.scanner;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.lazilette.presencode.R;
import com.lazilette.presencode.model.Absen;
import com.lazilette.presencode.model.User;
import com.lazilette.presencode.ui.geofences.GeofenceBroadcastReceiver;
import com.lazilette.presencode.ui.geofences.GeofenceHelper;
import com.lazilette.presencode.ui.geofences.MapsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private GoogleMap mMap;
    String absen,name,key,tanggal;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    private GeofenceHelper geofenceHelper;
    private GeofencingClient geofencingClient;
    private float geo_radius = 30;
    private String GEOFENCE_ID = "0609";
    private LatLng pinBKPSDM = new LatLng(-7.265680, 110.398875);
    private FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);


        setContentView(scannerView);


        getName();
        setTitle("Scanning barcode");
        scannerView.startCamera(0);
        Log.d("mylog", "sampe sini");
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        addGeofence();

        scannerView.setResultHandler(this);
        scannerView.startCamera(0); 
        doRequestPermission();


    }

    public void writeAbsen() {

        reference = FirebaseDatabase.getInstance().getReference().child("Absen").child(key+" "+tanggal);
        reference.setValue(absen);
        Toast.makeText(ScannerActivity.this, "Absen Masuk , berhasil !", Toast.LENGTH_SHORT).show();
        finish();


    }

    public void addGeofence() {
        Toast.makeText(ScannerActivity.this, "Mendeteksi lokasi user .. ", Toast.LENGTH_SHORT).show();
        Log.d("mylog", "onSuccess: Geofence triggering added");
        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, pinBKPSDM, geo_radius,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Geofence added");
                    //    Toast.makeText(ScannerActivity.this, "geofence added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: fail");
                        String errorMsg = geofenceHelper.getErrorMessage(e);
                        Toast.makeText(ScannerActivity.this, "err.."+errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private void doRequestPermission() {
        int permissionCheckStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheckStorage == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    public void trackUserLocation() {
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }*/

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Toast.makeText(ScannerActivity.this, "Lokasi user ditemukan .. ", Toast.LENGTH_SHORT).show();
                        if (location != null) {
                            // Logic to handle location object
                        }
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    public void getName() {
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        key = currentUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference();
        String userKey = key;
        ref.child("Users").child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("username").getValue(String.class);
                Log.d(TAG, "Nameabsen: " + name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void handleResult(com.google.zxing.Result rawResult) {
        onResume();

        String hasil = rawResult.getText();
        StringTokenizer tokens = new StringTokenizer(hasil, "#");
        String kegiatan = tokens.nextToken();
        String kantor = tokens.nextToken();
        tanggal = new SimpleDateFormat("d-MM-yyyy", Locale.getDefault()).format(new Date());
        String jam = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        absen = name+" telah absen "+kegiatan + " " +kantor+" "+tanggal+" "+jam;
        Log.d("mylog",  absen);
        if(geofenceHelper.isTriggered()){
            writeAbsen();
        }
        else if(){
            Toast.makeText(ScannerActivity.this, "Pastikan anda di wilayah BKPSDM :)", Toast.LENGTH_LONG).show();
            finish();
        }




    }

}