package com.example.lve_videocallchat;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private Button save;
    private EditText edit_status;
    private EditText setusername;
    private ImageView profile_pic;
    private TextView change_profile_pic;
    private int gallery_count=1;
    private Uri imageuri;
    private int day,month,year;
    private Calendar cal;
    private StorageReference profile_pic_storage;
    private String downloadurl;
    private DatabaseReference user;
    ProgressDialog progressDialog;
    private int file_size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        profile_pic_storage= FirebaseStorage.getInstance().getReference().child("Profile_Pictures");
        user= FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog=new ProgressDialog(this);

        cal=Calendar.getInstance();
        save=findViewById(R.id.save_button);
        edit_status=findViewById(R.id.edit_status);
        setusername=findViewById(R.id.set_username);
        profile_pic=findViewById(R.id.profile_pic);
        change_profile_pic=findViewById(R.id.change_profile_pic);
        day=cal.get(Calendar.DAY_OF_MONTH);
        year=cal.get(Calendar.YEAR);
        month=cal.get(Calendar.MONTH);

        change_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toGallery=new Intent();
                toGallery.setAction(Intent.ACTION_GET_CONTENT);
                toGallery.setType("image/*");
                startActivityForResult(toGallery.createChooser(toGallery,"Select Image"),gallery_count);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        getData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallery_count && resultCode==RESULT_OK && data!=null)
        {
            imageuri=data.getData();
            profile_pic.setImageURI(imageuri);
        }
    }


    private void saveData() {


         final String getusername=setusername.getText().toString();
         final String updatestatus=edit_status.getText().toString();
         if(getusername.isEmpty())
         {
             setusername.setError("Please Add a UserName!");
         }
         else if(imageuri==null)
         {
             user.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {

                     if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("Profile_Pictures"))
                     {
                         savetextInfo();

                     }
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });
         }
         else
         {
             progressDialog.setTitle("Settings");
             progressDialog.setMessage("Saving your settings...");
             progressDialog.show();
             final StorageReference storageReference=profile_pic_storage.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
             final UploadTask uploadTask=storageReference.putFile(imageuri);
             //File file = new File(String.valueOf(imageuri));
             //file_size = Integer.parseInt(String.valueOf(file.length()/1024));

             uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                 @Override
                 public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                     if(!task.isSuccessful())
                     {
                         throw task.getException();
                     }
                     downloadurl=storageReference.getDownloadUrl().toString();
                     return storageReference.getDownloadUrl();
                 }
             }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                 @Override
                 public void onComplete(@NonNull Task<Uri> task) {
                     if(task.isSuccessful())
                     {
                         downloadurl=task.getResult().toString();
                         HashMap<String,Object> hashMap=new HashMap<>();
                         hashMap.put("UID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                         hashMap.put("UserName",getusername);
                         hashMap.put("Status",updatestatus);
                         hashMap.put("Profile_Pic",downloadurl);

                         user.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                 .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful())
                                 {
                                     Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
                                     startActivity(intent);
                                     finish();
                                     progressDialog.dismiss();

                                     Toast.makeText(SettingsActivity.this,"Profile Settings Updated!",Toast.LENGTH_LONG).show();
                                 }

                             }
                         });
                     }
                 }
             });
         }
    }

    private void savetextInfo() {
        final String getusername=setusername.getText().toString();
        final String updatestatus=edit_status.getText().toString();

        if(getusername==null)
        {
            setusername.setError("Please Add a UserName!");
        }
        else
        {
            progressDialog.setTitle("Settings");
            progressDialog.setMessage("Saving your settings...");
            progressDialog.show();
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("UID",FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("UserName",getusername);
            hashMap.put("Status",updatestatus);

            user.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();

                        Toast.makeText(SettingsActivity.this,"Profile Settings Updated!",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }
    private void getData()
    {
        user.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String imagefromdb=dataSnapshot.child("Profile_Pic").getValue().toString();
                    String usernamefromdb=dataSnapshot.child("UserName").getValue().toString();
                    String statusfromdb=dataSnapshot.child("Status").getValue().toString();

                    Picasso.get().load(imagefromdb).placeholder(R.drawable.man).into(profile_pic);
                    setusername.setText(usernamefromdb);
                    edit_status.setText(statusfromdb);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

