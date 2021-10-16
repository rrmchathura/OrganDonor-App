package com.rrmchathura.organdonor_app.Donor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.rrmchathura.organdonor_app.Model.Users;
import com.rrmchathura.organdonor_app.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Donor_Update_Profile_Activity extends AppCompatActivity {

    CircleImageView profileimageview,uploadimage;
    EditText fullnameEt,phoneEt;
    TextView selectBloodGroupEt,btnUpdateProfile;
    ImageView backbtn;


    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ArrayList<String> arrayList;
    Dialog dialog;
    private Uri image_uri;

    Users users;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    String fullName, email, bloodGroup, type, mobile,id,password,userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_update_profile);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        users = new Users();

        init();

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        arrayList = new ArrayList<>();

        arrayList.add("A+");
        arrayList.add("A-");
        arrayList.add("B+");
        arrayList.add("B-");
        arrayList.add("AB+");
        arrayList.add("AB-");
        arrayList.add("O+");
        arrayList.add("O-");

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   showImagePickerDialog();
            }
        });

        selectBloodGroupEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(Donor_Update_Profile_Activity.this);
                dialog.setContentView(R.layout.dialog_searchable_bloodgroup);
                dialog.getWindow().setLayout(650, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Donor_Update_Profile_Activity.this, R.layout.listview_text_color, arrayList);
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

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataUpdateToDatabase();
            }
        });

    }

    private void DataUpdateToDatabase() {

        String fullNameTv = fullnameEt.getText().toString().trim();
        String mobileTv = phoneEt.getText().toString().trim();
        String bloodGroupTv = selectBloodGroupEt.getText().toString().trim();

        if (image_uri == null) {
            //upload without image


            users.setFullName(fullName);
            users.setMobile(mobileTv);
            users.setBloodGroup(bloodGroupTv);
            users.setUserID(userID);
            users.setUserType(type);
            users.setPassword(password);
            users.setEmail(email);
            users.setId(id);


            database.getReference().child("Users").child(mAuth.getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {


                    if (task.isSuccessful()) {

                        Toast.makeText(Donor_Update_Profile_Activity.this,"Updated Successfully",Toast.LENGTH_SHORT).show();

                    }

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(Donor_Update_Profile_Activity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            //upload with image
            String filepathname = "profile_images/";

            storage.getReference(filepathname).putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri downloadimageUri = uriTask.getResult();

                    if (uriTask.isSuccessful()) {

                        users.setFullName(fullName);
                        users.setMobile(mobileTv);
                        users.setBloodGroup(bloodGroupTv);


                        database.getReference().child("Users").child(mAuth.getUid()).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {

                                Toast.makeText(Donor_Update_Profile_Activity.this,"Update success with image",Toast.LENGTH_SHORT).show();
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(Donor_Update_Profile_Activity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(Donor_Update_Profile_Activity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showImagePickerDialog() {

        String[] options = {"camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (checkCameraPermissions()) {
                        pickFromCamera();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    if (checkStoragePermission()) {
                        pickFromGallery();

                    } else {
                        requestStoragePermission();
                    }

                }
            }
        });
        builder.show();
    }


    private void init(){

        profileimageview = findViewById(R.id.profileimageview);
        uploadimage = findViewById(R.id.uploadimage);
        fullnameEt = findViewById(R.id.fullnameEt);
        phoneEt = findViewById(R.id.phoneEt);
        selectBloodGroupEt = findViewById(R.id.selectBloodGroupEt);
        backbtn = findViewById(R.id.backbtn);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
    }

    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");

        image_uri = getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions() {

        boolean result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;

    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getApplicationContext(), "Camera & Storage Permissions are Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getApplicationContext(), "Camera & Storage Permissions are Required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();

               profileimageview.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                profileimageview.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    protected void onStart() {
        super.onStart();
        LoaduserDetails();
    }

    private void LoaduserDetails() {

        database.getReference().child("Users").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    fullName = snapshot.child("fullName").getValue().toString();
                    email = snapshot.child("email").getValue().toString();
                    bloodGroup = snapshot.child("bloodGroup").getValue().toString();
                    type = snapshot.child("userType").getValue().toString();
                    mobile = snapshot.child("mobile").getValue().toString();
                    id = snapshot.child("id").getValue().toString();
                    password = snapshot.child("password").getValue().toString();
                    userID = snapshot.child("userID").getValue().toString();


                   fullnameEt.setText(fullName);
                   phoneEt.setText(mobile);
                   selectBloodGroupEt.setText(bloodGroup);

                    try {
                        String profile_image = snapshot.child("profile_image").getValue().toString();
                        Picasso.get().load(profile_image).placeholder(R.drawable.profile).into(profileimageview);

                    } catch (Exception e) {

                        profileimageview.setImageResource(R.drawable.profile);

                    }
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}