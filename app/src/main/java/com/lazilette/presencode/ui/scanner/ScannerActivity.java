package com.lazilette.presencode.ui.scanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;
import com.lazilette.presencode.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    String absen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);

        setContentView(scannerView);


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

    @Override
    public void handleResult(com.google.zxing.Result rawResult) {
        onResume();
        String hasil = rawResult.getText();
        StringTokenizer tokens = new StringTokenizer(hasil, "#");
        String kegiatan = tokens.nextToken();
        String kantor = tokens.nextToken();
        String tanggal = new SimpleDateFormat("d-MM-yyyy", Locale.getDefault()).format(new Date());
        String jam = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        absen = kegiatan + " " +kantor+" "+tanggal+" "+jam;
        Log.d("mylog",  absen);
        finish();


    }
}