package com.example.adminpanel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.adminpanel.Facullty.UpdateFacullty;
import com.example.adminpanel.Notice.DeleteNoticeActivity;
import com.example.adminpanel.Notice.UploadNotice;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CardView addNotice, addGallery, addEBook, addFacullty, deleteNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addNotice = findViewById(R.id.addNotice);
        addGallery = findViewById(R.id.addGallery);
        addEBook = findViewById(R.id.addEBook);
        addFacullty = findViewById(R.id.addFacullty);
        deleteNotice = findViewById(R.id.deleteNotice);

        addNotice.setOnClickListener(this);
        addGallery.setOnClickListener(this);
        addEBook.setOnClickListener(this);
        addFacullty.setOnClickListener(this);
        deleteNotice.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.addNotice:
                intent = new Intent(MainActivity.this, UploadNotice.class);
                startActivity(intent);
                break;
            case R.id.addGallery:
                intent = new Intent(MainActivity.this, UploadImage.class);
                startActivity(intent);
                break;
            case R.id.addEBook:
                intent = new Intent(MainActivity.this, UploadPDF.class);
                startActivity(intent);
                break;
            case R.id.addFacullty:
                intent = new Intent(MainActivity.this, UpdateFacullty.class);
                startActivity(intent);
                break;
            case R.id.deleteNotice:
                intent = new Intent(MainActivity.this, DeleteNoticeActivity.class);
                startActivity(intent);
                break;
        }
    }
}