package com.example.adminpanel.Notice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadNotice extends AppCompatActivity {

    private CardView addImage;
    private Bitmap bitmap;
    private ImageView imageView_Notice;
    private TextInputEditText notice_Title;
    private MaterialButton btn_UploadNotice;
    private final int REQ = 1;
    String downloadUrl = "";
    private ProgressDialog progressDialog;
    private DatabaseReference reference, dbRef;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);
        addImage = findViewById(R.id.addImage);
        imageView_Notice = findViewById(R.id.imageView_Notice);
        notice_Title = findViewById(R.id.notice_Title);
        btn_UploadNotice = findViewById(R.id.btn_UploadNotice);
        reference = FirebaseDatabase.getInstance("https://adminpanel-c4498-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btn_UploadNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notice_Title.getText().toString().isEmpty() || bitmap == null){
                    notice_Title.setError("Empty");
                    notice_Title.requestFocus();
                    Toast.makeText(UploadNotice.this, "Please upload image and input title !!!", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                } else {
                    uploadImage();
                }
            }
        });
    }

    private void uploadData() {
        dbRef = reference.child("Notice");
        final String uniqueKey = dbRef.push().getKey();

        String title = notice_Title.getText().toString();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        String date = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time = currentTime.format(calForTime.getTime());

        NoticeData noticeData = new NoticeData(title, downloadUrl, date, time, uniqueKey);

        Toast.makeText(this, "Data Uploading", Toast.LENGTH_SHORT).show();
        dbRef.child(uniqueKey).setValue(noticeData).addOnCompleteListener(task -> {
           if(task.isComplete()){
               progressDialog.dismiss();
               Toast.makeText(UploadNotice.this, "Notice Uploaded !!!", Toast.LENGTH_SHORT).show();
               finish();
               startActivity(getIntent());
           }else {
               progressDialog.dismiss();
               Toast.makeText(UploadNotice.this, "Something went wrong !!!", Toast.LENGTH_SHORT).show();
           }
        });
    }

    private void uploadImage() {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] finalimg = baos.toByteArray();
        final StorageReference filePath;
        filePath = storageReference.child("Notice").child(finalimg+"jpg");
        final UploadTask uploadTask = filePath.putBytes(finalimg);
        uploadTask.addOnCompleteListener(UploadNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnCompleteListener(UploadNotice.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = String.valueOf(uri);
                                    uploadData();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(UploadNotice.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
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
            imageView_Notice.setImageBitmap(bitmap);
        }
    }
}