package com.example.adminpanel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadPDF extends AppCompatActivity {

    private CardView addPDF;
    private TextInputEditText pdf_Title;
    private MaterialButton btn_UploadPDF;
    private DatabaseReference databaseReference;
    private final int REQ = 1;
    private Uri pdfData;
    private StorageReference storageReference;
    private String pdfName, title;
    private ProgressDialog progressDialog;
    private TextView textView_PDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);
        addPDF = findViewById(R.id.addPDF);
        pdf_Title = findViewById(R.id.pdf_Title);
        btn_UploadPDF = findViewById(R.id.btn_UploadPDF);
        textView_PDF = findViewById(R.id.textView_PDF);
        databaseReference = FirebaseDatabase.getInstance("https://adminpanel-c4498-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        addPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btn_UploadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = pdf_Title.getText().toString();
                if(title.isEmpty()){
                    pdf_Title.setError("Empty!!!");
                    pdf_Title.requestFocus();
                }else if(pdfData == null){
                    Toast.makeText(UploadPDF.this, "Please Upload PDF File !!!", Toast.LENGTH_SHORT).show();
                }else {
                    uploadPDF();
                }

            }
        });
    }

    private void uploadPDF() {
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Uploading PDF File...");
        progressDialog.show();
        StorageReference reference = storageReference.child("E-Book/"+pdfName+"-"+System.currentTimeMillis()+".pdf");
        reference.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri uri = uriTask.getResult();
                uploadData(String.valueOf(uri));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadPDF.this, "Something went wrong !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData(String downloadUrl) {
        String uniqueKey = databaseReference.child("E-Book").push().getKey();
        HashMap data = new HashMap();
        data.put("pdfTitle",title);
        data.put("pdfUrl",downloadUrl);

        databaseReference.child("E-Book").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(UploadPDF.this, "Successfully Uploaded PDF File !!!", Toast.LENGTH_SHORT).show();
                pdf_Title.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadPDF.this, "Failed to Upload PDF File !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF File !!!"), REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ && resultCode == RESULT_OK){
            pdfData = data.getData();
            if(pdfData.toString().startsWith("content://")){
                Cursor cursor = null;
                try {
                    cursor = UploadPDF.this.getContentResolver().query(pdfData, null, null, null, null);
                    if(cursor != null && cursor.moveToFirst()){
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(pdfData.toString().startsWith("file://")){
                pdfName = new File(pdfData.toString()).getName();
            }

            textView_PDF.setText(pdfName);
            Toast.makeText(this, ""+pdfData, Toast.LENGTH_SHORT).show();
        }
    }
}