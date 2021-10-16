package com.rrmchathura.organdonor_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText fullnameEt, emailEt, idEt, phoneEt, passwordEt, conPasswordEt;
    TextView selectBloodGroupEt, btnRegister,btnsignup;
    RadioButton donorRb, receptionRb;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    LazyLoader Loader;

    ArrayList<String> arrayList;
    Dialog dialog;

    private String userType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        arrayList = new ArrayList<>();

        arrayList.add("A+");
        arrayList.add("A-");
        arrayList.add("B+");
        arrayList.add("B-");
        arrayList.add("AB+");
        arrayList.add("AB-");
        arrayList.add("O+");
        arrayList.add("O-");

        LazyLoader loader = new LazyLoader(this, 10, 10, ContextCompat.getColor(this, R.color.loader_selected),
                ContextCompat.getColor(this, R.color.loader_selected),
                ContextCompat.getColor(this, R.color.loader_selected));
        loader.setAnimDuration(500);
        loader.setFirstDelayDuration(100);
        loader.setSecondDelayDuration(200);
        loader.setInterpolator(new LinearInterpolator());

        Loader.addView(loader);

        donorRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receptionRb.setChecked(false);
                userType = "Donor";
            }
        });

        receptionRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donorRb.setChecked(false);
                userType = "Reception";
            }
        });

        selectBloodGroupEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(RegisterActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_bloodgroup);
                // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setLayout(650, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,R.layout.listview_text_color,arrayList);
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectBloodGroupEt.setText(adapter.getItem(i));
                        dialog.dismiss();
                    }
                });


            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    private void validateData() {

        final String fullName = fullnameEt.getText().toString().trim();
        final String email = emailEt.getText().toString().trim();
        final String id = idEt.getText().toString().trim();
        final String mobile = phoneEt.getText().toString().trim();
        final String bloodGroup = selectBloodGroupEt.getText().toString().trim();
        final String password = passwordEt.getText().toString().trim();
        final String con_password = conPasswordEt.getText().toString().trim();
        String usertype = userType.toString().trim();


        if (TextUtils.isEmpty(fullName)){
            fullnameEt.setError("Full Name Required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Email Required");
            return;
        }
        if (TextUtils.isEmpty(id)) {
            idEt.setError("Id Required");
            return;
        }
        if (TextUtils.isEmpty(mobile)) {
            phoneEt.setError("Mobile Required");
            return;
        }
        if (mobile.length() < 10){
            phoneEt.setError("Invalid Number");
            return;
        }
        if (TextUtils.isEmpty(bloodGroup)) {
            Toast.makeText(RegisterActivity.this,"Blood Group Required",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Password Required");
            return;
        }
        if (password.length() < 6){
            passwordEt.setError("Password  Must be >= 6 Characters");
        }
        if (TextUtils.isEmpty(con_password)) {
            conPasswordEt.setError("Confirm Password Required");
            return;
        }
        if (!password.equals(con_password)){
            Toast.makeText(RegisterActivity.this,"Please check password",Toast.LENGTH_SHORT).show();
            return;
        }

        if (userType.isEmpty()){
            Toast.makeText(RegisterActivity.this,"Please Select Type",Toast.LENGTH_SHORT).show();
            return;
        }
        else {

            SaveDataToDatabase(email,password,fullName,id,mobile,bloodGroup);
        }
    }

    private void SaveDataToDatabase(String email, String password, String fullName, String id, String mobile, String bloodGroup) {

        Loader.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailEt.getText().toString(),passwordEt.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    DatabaseReference reference = database.getReference().child("Users").child(mAuth.getUid());
                    HashMap<String,Object> data = new HashMap<>();
                    data.put("fullName",fullName);
                    data.put("email",email);
                    data.put("id",id);
                    data.put("mobile",mobile);
                    data.put("bloodGroup",bloodGroup);
                    data.put("password",password);
                    data.put("userType",userType);
                    data.put("userID",mAuth.getUid());

                    reference.updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Loader.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this,"Create Account Successfully",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Loader.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                            Log.d("TAG",e.getMessage());

                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Loader.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("TAG1",e.getMessage());
            }
        });
    }

    private void init() {

        fullnameEt = findViewById(R.id.fullnameEt);
        emailEt = findViewById(R.id.emailEt);
        idEt = findViewById(R.id.idEt);
        phoneEt = findViewById(R.id.phoneEt);
        passwordEt = findViewById(R.id.passwordEt);
        conPasswordEt = findViewById(R.id.conPasswordEt);
        selectBloodGroupEt = findViewById(R.id.selectBloodGroupEt);
        btnRegister = findViewById(R.id.btnRegister);
        donorRb = findViewById(R.id.donorRb);
        receptionRb = findViewById(R.id.receptionRb);
        Loader = findViewById(R.id.loader);
        btnsignup = findViewById(R.id.btnsignup);

    }
}