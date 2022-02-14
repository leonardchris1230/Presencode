package com.lazilette.presencode.ui.home;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    String hour,name;
    ActivityHomeBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getHour();
        getName();

        binding.keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
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

    public void logout(){
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }
}