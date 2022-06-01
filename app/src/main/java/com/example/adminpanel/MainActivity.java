package com.example.adminpanel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addNotice:
                Intent intent = new Intent(MainActivity.this, UploadNotice.class);
                startActivity(intent);
                break;
        }
    }
}