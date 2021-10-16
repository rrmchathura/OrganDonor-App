package com.rrmchathura.organdonor_app.Donor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rrmchathura.organdonor_app.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class Donor_Profile_Activity extends AppCompatActivity {

    TextView usernameTv,emailtv,mobileTv,bloodGroupTv,userTypeTv,updateProfileBtn;
    CircleImageView profileImage;
    ImageView backbtn;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);

        init();


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users");

        LoaduserDetails();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Donor_Profile_Activity.this,Donor_Update_Profile_Activity.class);
                startActivity(intent);
            }
        });


    }

    private void LoaduserDetails() {

        reference.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                String fullName = snapshot.child("fullName").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String bloodGroup = snapshot.child("bloodGroup").getValue().toString();
                String type = snapshot.child("userType").getValue().toString();
                String mobile = snapshot.child("mobile").getValue().toString();


                usernameTv.setText(fullName);
                emailtv.setText(email);
                mobileTv.setText(mobile);
                bloodGroupTv.setText(bloodGroup);
                userTypeTv.setText(type);

                try {
                    String profile_image = snapshot.child("profile_image").getValue().toString();
                    Picasso.get().load(profile_image).placeholder(R.drawable.profile).into(profileImage);

                }catch (Exception e){

                    profileImage.setImageResource(R.drawable.profile);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private void init(){

        usernameTv = findViewById(R.id.usernameTv);
        emailtv = findViewById(R.id.emailtv);
        mobileTv = findViewById(R.id.mobileTv);
        bloodGroupTv = findViewById(R.id.bloodGroupTv);
        userTypeTv = findViewById(R.id.userTypeTv);
        profileImage = findViewById(R.id.profileImage);
        backbtn = findViewById(R.id.backbtn);
        updateProfileBtn = findViewById(R.id.updateProfileBtn);

    }

}