package com.lazilette.presencode.ui.scanner;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    String absen,name,key,tanggal;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    User u;
    List<Absen> absens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);

        setContentView(scannerView);


        getName();
        setTitle("Scanning barcode");
        scannerView.startCamera(0);
        Log.d("mylog", "sampe sini");

    }

    @Override
    public void onStart() {
        super.onStart();
        scannerView.setResultHandler(this);
        scannerView.startCamera(0); // 0 Kamera Belakang dan 1 itu kemera depan
        doRequestPermission();

    }

    public void writeAbsen() {

        reference = FirebaseDatabase.getInstance().getReference().child("Absen").child(key+" "+tanggal);

        reference.setValue(absen);
    }

    private void doRequestPermission() {
        int permissionCheckStorage = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionCheckStorage == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
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
        writeAbsen();
        finish();


    }

}