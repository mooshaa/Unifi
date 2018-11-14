package com.unifi.comp590.unifi;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity {

    private CircleImageView settingsImage;
    private EditText settingsName;
    private EditText settingsStatus;
    private Button settingsSave;
    private DatabaseReference mDatabaseReference;
    private StorageReference mImageStorageReference;
    private FirebaseAuth mAuth;
    private final static int GALLERY_CODE=1;
    private ImageButton changeImageOpaque;
    private ImageButton changeImageTransparent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mImageStorageReference = FirebaseStorage.getInstance().getReference().child("Profile_Image");


        changeImageOpaque = (ImageButton) findViewById(R.id.settings_image_edit_button_opaque);
        changeImageTransparent = (ImageButton) findViewById(R.id.settings_image_edit_button_transparent);
//   TODO     changeImageTransparent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        changeImageTransparent.setVisibility(View.GONE);
//                        changeImageOpaque.setVisibility(View.VISIBLE);
//                    case MotionEvent.ACTION_UP:
//                        changeImageTransparent.setVisibility(View.VISIBLE);
//                        changeImageOpaque.setVisibility(View.GONE);
//            }
//        })
        settingsImage = (CircleImageView) findViewById(R.id.account_settings_user_image);
        settingsName = (EditText) findViewById(R.id.account_settings_username);
        settingsStatus = (EditText) findViewById(R.id.account_settings_status);
        settingsSave = (Button) findViewById(R.id.account_settings_button_save);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();
                String thumbnail = dataSnapshot.child("user_thumbnail").getValue().toString();
                settingsName.setText(name);
                settingsStatus.setText(status);
                Picasso.get().load(image).into(settingsImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        settingsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Choose Picture"), GALLERY_CODE);

            }
        });

        settingsSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = settingsName.getText().toString();
                String status = settingsStatus.getText().toString();
                UpdateEntries(name, status);
            }
        });
    }

    private void UpdateEntries(String name, String status) {
        final AlertDialog mLoading = LoadingCircle.Circle(SettingsActivity.this);

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Username cant be empty", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(status)) {
            Toast.makeText(this, "Status cant be empty", Toast.LENGTH_SHORT).show();
        } else {
            mLoading.show();
            mDatabaseReference.child("user_status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mLoading.dismiss();
                        Toast.makeText(SettingsActivity.this, "Changes Saved", Toast.LENGTH_LONG).show();
                        SettingsActivity.this.finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                String user_id = mAuth.getCurrentUser().getUid();
                StorageReference filePath = mImageStorageReference.child(user_id+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            String dlURL = task.getResult().getStorage().getDownloadUrl().toString();
                            mDatabaseReference.child("user_image").setValue(dlURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(SettingsActivity.this, "Profile picture updated.", Toast.LENGTH_LONG).show();

                                }
                            });
                        } else {
                            Toast.makeText(SettingsActivity.this, "Error while updating picture", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }
}
