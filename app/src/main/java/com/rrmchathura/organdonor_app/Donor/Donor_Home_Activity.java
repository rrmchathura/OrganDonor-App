package com.rrmchathura.organdonor_app.Donor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rrmchathura.organdonor_app.LoginActivity;
import com.rrmchathura.organdonor_app.R;

import org.jetbrains.annotations.NotNull;

public class Donor_Home_Activity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener  {

    FirebaseAuth mAuth;
    FirebaseDatabase database;

    androidx.appcompat.widget.Toolbar toolbar;
    DrawerLayout drawer_layout;
    NavigationView nav_view;

    private static final String FILE_NAME = "myFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home);

        toolbar = findViewById(R.id.toolbar);
        drawer_layout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);


        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        String email = sharedPreferences.getString("email","Data not found");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //Drawer hooks
        setSupportActionBar(toolbar);

        ///////////////////////////   //Navigation Drawer Menu///////////
        nav_view.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer_layout,toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        nav_view.setNavigationItemSelectedListener(this);
        updateNavHeader();
    }

    private void updateNavHeader() {

        View headerView = nav_view.getHeaderView(0);
        TextView nav_fullName = (TextView) headerView.findViewById(R.id.nav_fullName);
        TextView nav_email = (TextView) headerView.findViewById(R.id.nav_email);
        TextView nav_bloodGroup = (TextView) headerView.findViewById(R.id.nav_bloodGroup);
        TextView nav_type = (TextView) headerView.findViewById(R.id.nav_type);

        DatabaseReference reference = database.getReference().child("Users").child(mAuth.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String fullname = snapshot.child("fullName").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    String bloodGroup = snapshot.child("bloodGroup").getValue().toString();
                    String type = snapshot.child("userType").getValue().toString();

                    nav_fullName.setText(fullname);
                    nav_email.setText(email);
                    nav_bloodGroup.setText(bloodGroup);
                    nav_type.setText(type);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                mAuth.signOut();
                Intent intent = new Intent(Donor_Home_Activity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.nav_profile:
                Intent intent1 = new Intent(Donor_Home_Activity.this, Donor_Profile_Activity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);


//                replaceFragment(new DonorProfileFragment());
                break;

        }

        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }
}