package com.lazilette.presencode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lazilette.presencode.ui.home.HomeActivity;
import com.lazilette.presencode.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // redirect if user is not null
        if (firebaseUser != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}