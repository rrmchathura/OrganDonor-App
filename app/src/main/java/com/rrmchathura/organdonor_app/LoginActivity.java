package com.rrmchathura.organdonor_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rrmchathura.organdonor_app.Donor.Donor_Home_Activity;
import com.rrmchathura.organdonor_app.Reception.Reception_Home_Activity;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt,passwordEt;
    CheckBox remember_Me;
    TextView btnlogin,signupBtn;
    LazyLoader Loader;

    FirebaseAuth mAuth;
    FirebaseDatabase database;

    private static final String FILE_NAME = "myFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        init();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        String email = sharedPreferences.getString("email","");
        String password = sharedPreferences.getString("password","");

        emailEt.setText(email);
        passwordEt.setText(password);

        if (!email.isEmpty() || !password.isEmpty()){
            remember_Me.setChecked(!remember_Me.isChecked());
        }else {
            remember_Me.setChecked(remember_Me.isChecked());
        }


        LazyLoader loader = new LazyLoader(this, 10, 10, ContextCompat.getColor(this, R.color.loader_selected),
                ContextCompat.getColor(this, R.color.loader_selected),
                ContextCompat.getColor(this, R.color.loader_selected));
        loader.setAnimDuration(500);
        loader.setFirstDelayDuration(100);
        loader.setSecondDelayDuration(200);
        loader.setInterpolator(new LinearInterpolator());

        Loader.addView(loader);


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validateData() {
        String email = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        if (remember_Me.isChecked())
        {
            StoreDataUsingSharedPref(email,password);


        }
        if (TextUtils.isEmpty(email)){
            emailEt.setError("Email is Required");
            return;
        }
        if (TextUtils.isEmpty(password)){
            passwordEt.setError("Password is Required");
            return;
        }
        if (password.length() < 6){
            passwordEt.setError("Password  Must be >= 6 Characters");
        }
        else {
            LoginUser(email,password);
        }
    }

    private void LoginUser(String email, String password) {

        Loader.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    DatabaseReference reference = database.getReference("Users");
                    reference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String retrieveUserType = snapshot.child("userType").getValue().toString();

                                if (retrieveUserType.equals("Donor")){
                                    Intent intent = new Intent(LoginActivity.this, Donor_Home_Activity.class);
                                    // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else if (retrieveUserType.equals("Reception")){
                                    Intent intent = new Intent(LoginActivity.this, Reception_Home_Activity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    Loader.setVisibility(View.INVISIBLE);
                                    Toast toast = Toast.makeText(LoginActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT);
                                    toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                    toast.show();
                                }
                            }
                            else {
                                Loader.setVisibility(View.INVISIBLE);
                                Toast toast = Toast.makeText(LoginActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT);
                                toast.getView().setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                toast.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                Loader.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void StoreDataUsingSharedPref(String email, String password) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_NAME,MODE_PRIVATE).edit();
        editor.putString("email",email);
        editor.putString("password",password);
        editor.apply();
    }


    private void init(){

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        remember_Me = findViewById(R.id.remember_Me);
        btnlogin = findViewById(R.id.btnlogin);
        Loader = findViewById(R.id.loader);
        signupBtn = findViewById(R.id.signupBtn);

    }
}