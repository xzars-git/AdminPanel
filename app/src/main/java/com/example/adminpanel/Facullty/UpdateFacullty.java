package com.example.adminpanel.Facullty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.adminpanel.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateFacullty extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView matematika_Departement, sejarah_Departement, bahasa_Departement;
    private LinearLayout bahasa_NoData, matematika_NoData, sejarah_NoData;
    private List<TeacherData> mtk, sejarah, bahasa;
    private DatabaseReference reference, dbRef;
    private TeacherAdapter teacherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_facullty);
        fab = findViewById(R.id.fab);
        matematika_Departement = findViewById(R.id.matematika_Departement);
        sejarah_Departement = findViewById(R.id.sejarah_Departement);
        bahasa_Departement = findViewById(R.id.bahasa_Departement);
        bahasa_NoData = findViewById(R.id.bahasa_NoData);
        matematika_NoData = findViewById(R.id.matematika_NoData);
        sejarah_NoData = findViewById(R.id.sejarah_NoData);

        reference = FirebaseDatabase.getInstance("https://adminpanel-c4498-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Teacher");

        matematika_Departement();
        sejarah_Departement();
        bahasa_Departement();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateFacullty.this, AddTeacher.class));
            }
        });
    }

    private void matematika_Departement() {
        dbRef = reference.child("Matematika");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mtk = new ArrayList<>();
                if(!snapshot.exists()){
                    matematika_NoData.setVisibility(View.VISIBLE);
                    matematika_Departement.setVisibility(View.GONE);
                }else {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        TeacherData data = dataSnapshot.getValue(TeacherData.class);
                        mtk.add(data);
                    }
                    matematika_Departement.setHasFixedSize(true);
                    matematika_Departement.setLayoutManager(new LinearLayoutManager(UpdateFacullty.this));
                    teacherAdapter = new TeacherAdapter(mtk, UpdateFacullty.this, "Matematika");
                    matematika_Departement.setAdapter(teacherAdapter);
                    matematika_NoData.setVisibility(View.GONE);
                    matematika_Departement.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFacullty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void bahasa_Departement() {
        dbRef = reference.child("Bahasa Indonesia");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bahasa = new ArrayList<>();
                if(!snapshot.exists()){
                    bahasa_NoData.setVisibility(View.VISIBLE);
                    bahasa_Departement.setVisibility(View.GONE);
                }else {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        TeacherData data = dataSnapshot.getValue(TeacherData.class);
                        bahasa.add(data);
                    }
                    bahasa_Departement.setHasFixedSize(true);
                    bahasa_Departement.setLayoutManager(new LinearLayoutManager(UpdateFacullty.this));
                    teacherAdapter = new TeacherAdapter(bahasa, UpdateFacullty.this, "Bahasa Indonesia");
                    bahasa_Departement.setAdapter(teacherAdapter);
                    bahasa_NoData.setVisibility(View.GONE);
                    bahasa_Departement.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFacullty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sejarah_Departement() {
        dbRef = reference.child("Sejarah");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sejarah = new ArrayList<>();
                if(!snapshot.exists()){
                    sejarah_NoData.setVisibility(View.VISIBLE);
                    sejarah_Departement.setVisibility(View.GONE);
                }else {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        TeacherData data = dataSnapshot.getValue(TeacherData.class);
                        sejarah.add(data);
                    }
                    sejarah_Departement.setHasFixedSize(true);
                    sejarah_Departement.setLayoutManager(new LinearLayoutManager(UpdateFacullty.this));
                    teacherAdapter = new TeacherAdapter(sejarah, UpdateFacullty.this, "Sejarah");
                    sejarah_Departement.setAdapter(teacherAdapter);
                    sejarah_NoData.setVisibility(View.GONE);
                    sejarah_Departement.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateFacullty.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}