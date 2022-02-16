package com.lazilette.presencode.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lazilette.presencode.R;
import com.lazilette.presencode.databinding.ActivityLoginBinding;
import com.lazilette.presencode.ui.home.HomeActivity;
import com.lazilette.presencode.ui.password.ForgetPasswordActivity;
import com.lazilette.presencode.ui.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    DatabaseReference reference;
    Dialog dialogE,dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        dialog = new Dialog(LoginActivity.this);

        binding.loginBuatAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        binding.loginLupaPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });

        binding.buttonMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setMessage("Mohon Tunggu Sebentar...");
                pd.show();

                String email = binding.loginId.getText().toString();
                String password = binding.LoginPass.getText().toString();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "Mohon isi semua field", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(auth.getCurrentUser().getUid());

                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                pd.dismiss();
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                pd.dismiss();
                                            }
                                        });

                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(LoginActivity.this, "Login gagal", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }


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

    @Override
    public void onBackPressed() {
        exitDialog();
    }
}