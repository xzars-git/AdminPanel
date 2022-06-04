package com.example.adminpanel.Notice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.adminpanel.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteNoticeActivity extends AppCompatActivity {

    private RecyclerView recyclerView_DeleteNotice;
    private ProgressBar progress_Bar;
    private ArrayList<NoticeData> list;
    private NoticeAdapter adapter;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_notice);
        recyclerView_DeleteNotice = findViewById(R.id.recyclerView_DeleteNotice);
        progress_Bar = findViewById(R.id.progress_Bar);
        reference = FirebaseDatabase.getInstance("https://adminpanel-c4498-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("Notice");
        recyclerView_DeleteNotice.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_DeleteNotice.setHasFixedSize(true);
        getNotice();
    }

    private void getNotice() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for (DataSnapshot snapshotData : snapshot.getChildren()){
                    NoticeData data = snapshotData.getValue(NoticeData.class);
                    list.add(data);
                }

                adapter = new NoticeAdapter(DeleteNoticeActivity.this, list);
                adapter.notifyDataSetChanged();
                progress_Bar.setVisibility(View.GONE);
                recyclerView_DeleteNotice.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DeleteNoticeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}