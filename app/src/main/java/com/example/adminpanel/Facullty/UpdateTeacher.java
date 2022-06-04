package com.example.adminpanel.Facullty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.adminpanel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateTeacher extends AppCompatActivity {

    private CircleImageView update_TeacherImage;
    private TextInputEditText update_TeacherName, update_TeacherEmail, update_TeacherPost;
    private MaterialButton btn_UpdateProfile, btn_DeleteProfile;

    private String name, email, post, image, downloadURl="", uniqueKey, category;
    private final int REQ = 1;
    private Bitmap bitmap = null;
    private ProgressDialog progressDialog;
    private DatabaseReference reference, dbRef;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_teacher);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        post = getIntent().getStringExtra("post");
        image = getIntent().getStringExtra("image");
        uniqueKey = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");

        update_TeacherImage = findViewById(R.id.update_TeacherImage);
        update_TeacherName = findViewById(R.id.update_TeacherName);
        update_TeacherEmail = findViewById(R.id.update_TeacherEmail);
        update_TeacherPost = findViewById(R.id.update_TeacherPost);
        btn_UpdateProfile = findViewById(R.id.btn_UpdateProfile);
        btn_DeleteProfile = findViewById(R.id.btn_DeleteProfile);
        progressDialog = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance("https://adminpanel-c4498-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Teacher");
        storageReference = FirebaseStorage.getInstance().getReference();

        try {
            Picasso.get().load(image).into(update_TeacherImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        update_TeacherEmail.setText(email);
        update_TeacherName.setText(name);
        update_TeacherPost.setText(post);

        update_TeacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btn_UpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = update_TeacherName.getText().toString();
                email = update_TeacherEmail.getText().toString();
                post = update_TeacherPost.getText().toString();
                checkValidation();
            }
        });

        btn_DeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });
    }

    private void checkValidation() {
        if(name.isEmpty()){
            update_TeacherName.setError("Empty!!!");
            update_TeacherName.requestFocus();
        }else if(email.isEmpty()){
            update_TeacherEmail.setError("Empty!!!");
            update_TeacherEmail.requestFocus();
        }else if(post.isEmpty()){
            update_TeacherPost.setError("Empty!!!");
            update_TeacherPost.requestFocus();
        }else if(bitmap == null){
            updateData(image);
        }else {
            uploadImage();
        }
    }

    private void uploadImage() {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Teacher").child(finalimg+"jpg");
        final UploadTask uploadTask = filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(UpdateTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnCompleteListener(UpdateTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadURl = String.valueOf(uri);
                                    updateData(downloadURl);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(UpdateTeacher.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateData(String s) {

        HashMap hp = new HashMap();
        hp.put("name",name);
        hp.put("email",email);
        hp.put("post",post);
        hp.put("image",s);

        reference.child(category).child(uniqueKey).updateChildren(hp).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(UpdateTeacher.this, "Teacher Updated Successfully !!!", Toast.LENGTH_SHORT).show();
                Intent intent =  new Intent(UpdateTeacher.this, UpdateFacullty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateTeacher.this, "Something Went Wrong !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteData() {
        reference.child(category).child(uniqueKey).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UpdateTeacher.this, "Teacher Deleted Successfully !!!", Toast.LENGTH_SHORT).show();
                        Intent intent =  new Intent(UpdateTeacher.this, UpdateFacullty.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateTeacher.this, "Something Went Wrong !!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void openGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            update_TeacherImage.setImageBitmap(bitmap);
        }
    }
}