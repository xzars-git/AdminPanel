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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.adminpanel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddTeacher extends AppCompatActivity {

    private CircleImageView circleImageView_AddTeacher;
    private TextInputEditText textInputEditText_TeacherName, textInputEditText_TeacherEmail, textInputEditText_TeacherPost;
    private Spinner teacher_Category;
    private MaterialButton btn_UploadTeacher;
    private final int REQ = 1;
    private Bitmap bitmap;
    private String category;
    private RelativeLayout layout_AddImageTeacher;
    private String name, email, post, downloadURl = "";
    private ProgressDialog progressDialog;
    private DatabaseReference reference, dbRef;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        circleImageView_AddTeacher = findViewById(R.id.circleImageView_AddTeacher);
        textInputEditText_TeacherName = findViewById(R.id.textInputEditText_TeacherName);
        textInputEditText_TeacherEmail = findViewById(R.id.textInputEditText_TeacherEmail);
        textInputEditText_TeacherPost = findViewById(R.id.textInputEditText_TeacherPost);
        teacher_Category = findViewById(R.id.teacher_Category);
        btn_UploadTeacher = findViewById(R.id.btn_UploadTeacher);
        layout_AddImageTeacher = findViewById(R.id.layout_AddImageTeacher);
        reference = FirebaseDatabase.getInstance("https://adminpanel-c4498-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Teacher");
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        layout_AddImageTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        String [] items = new String[] {"Sellect Category", "Bahasa Indonesia", "Matematika", "Sejarah"};

        teacher_Category.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items));
        teacher_Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = teacher_Category.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_UploadTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });
    }

    private void checkValidation() {
        name = textInputEditText_TeacherName.getText().toString();
        email = textInputEditText_TeacherEmail.getText().toString();
        post = textInputEditText_TeacherPost.getText().toString();

        if(name.isEmpty()){
            textInputEditText_TeacherName.setError("Empty");
            textInputEditText_TeacherName.requestFocus();
        }else if(email.isEmpty()){
            textInputEditText_TeacherEmail.setError("Empty");
            textInputEditText_TeacherEmail.requestFocus();
        }else if(post.isEmpty()){
            textInputEditText_TeacherPost.setError("Empty");
            textInputEditText_TeacherPost.requestFocus();
        }else if(category.equals("Sellect Category")){
            Toast.makeText(this, "Please Select Teacher Category !!!", Toast.LENGTH_SHORT).show();
        }else if(bitmap == null){
            Toast.makeText(this, "Please Select Teacher Image !!!", Toast.LENGTH_SHORT).show();
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
        uploadTask.addOnCompleteListener(AddTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnCompleteListener(AddTeacher.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadURl = String.valueOf(uri);
                                    uploadData();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(AddTeacher.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadData(){
        dbRef = reference.child(category);
        final String uniqueKey = dbRef.push().getKey();

        TeacherData teacherData = new TeacherData(name,email,post, downloadURl, uniqueKey);

        Toast.makeText(this, "Data Uploading", Toast.LENGTH_SHORT).show();
        dbRef.child(uniqueKey).setValue(teacherData).addOnCompleteListener(task -> {
            if(task.isComplete()){
                progressDialog.dismiss();
                Toast.makeText(AddTeacher.this, "Teacher Data Uploaded !!!", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }else {
                progressDialog.dismiss();
                Toast.makeText(AddTeacher.this, "Something went wrong !!!", Toast.LENGTH_SHORT).show();
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
            circleImageView_AddTeacher.setImageBitmap(bitmap);
        }
    }
}