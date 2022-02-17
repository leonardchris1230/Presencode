package com.lazilette.presencode.ui.home;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lazilette.presencode.R;
import com.lazilette.presencode.databinding.ActivityHomeBinding;
import com.lazilette.presencode.databinding.ActivityLoginBinding;
import com.lazilette.presencode.ui.login.LoginActivity;
import com.lazilette.presencode.ui.scanner.ScannerActivity;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    String hour,name;
    ActivityHomeBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    Dialog dialog, dialogLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getHour();
        getName();

        dialog = new Dialog(this);
        dialogLogout = new Dialog(this);

        binding.keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               logoutDialog();
            }
        });

        binding.homeExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitDialog();
            }
        });

        binding.btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ScannerActivity.class));
            }
        });
    }

    public void getHour() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            hour = "Selamat\nPagi,";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            hour = "Selamat\nSiang,";
        } else if (timeOfDay >= 16 && timeOfDay < 24) {
            hour = "Selamat\nMalam,";
        } else {
            hour = "Selamat\nDatang,";
        }

        binding.tvHome1.setText(hour);
    }

    public void getName() {
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String key = currentUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ref = firebaseDatabase.getReference();
        String userKey = key;
        ref.child("Users").child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("username").getValue(String.class);
                Log.d(TAG, "Nameeee: " + name);
                binding.nameDisplay.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void exitDialog() {
        dialog.setContentView(R.layout.alert_exit);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton yesExit = dialog.findViewById(R.id.btnExit);
        ImageButton noExit = dialog.findViewById(R.id.btnCLoseE);
        dialog.show();

        noExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        yesExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finishAffinity();
                finish();
            }
        });
    }

    private void logoutDialog() {
        dialogLogout.setContentView(R.layout.alert_logout);
        dialogLogout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton yesExit = dialogLogout.findViewById(R.id.btnLogout);
        ImageButton noExit = dialogLogout.findViewById(R.id.btnCLose);
        dialogLogout.show();

        noExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogLogout.dismiss();
            }
        });
        yesExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogLogout.dismiss();
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(HomeActivity.this, "Anda sudah berada di beranda", Toast.LENGTH_SHORT).show();
    }
}