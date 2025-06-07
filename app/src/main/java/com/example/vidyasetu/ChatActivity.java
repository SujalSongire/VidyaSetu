package com.example.vidyasetu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView groupRecycler;
    private FloatingActionButton createGroupBtn;
    private DatabaseReference groupsRef;
    private List<Group> groupList;
    private GroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        groupRecycler = findViewById(R.id.groupRecycler);
        createGroupBtn = findViewById(R.id.createGroupBtn);

        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, this);
        groupRecycler.setLayoutManager(new LinearLayoutManager(this));
        groupRecycler.setAdapter(groupAdapter);

        loadGroups();
        onStart();

        createGroupBtn.setOnClickListener(v -> {
            startActivity(new Intent(ChatActivity.this, CreateGroupActivity.class));
        });
    }
    private void loadGroups() {
        try {
            groupsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    groupList.clear();
                    for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                        Group group = groupSnapshot.getValue(Group.class);
                        if (group != null) {
                            groupList.add(group);
                        }
                    }
                    groupAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ChatActivity", "Error fetching groups: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ChatActivity", "Error loading groups", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if the user is authenticated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // User is authenticated, proceed to fetch groups data
            fetchGroupsData(); // Your method to fetch groups
        } else {
            // User is not authenticated, show a message or redirect to login
            Log.e("ChatActivity", "User not authenticated");
            Toast.makeText(this, "Please login to access the groups", Toast.LENGTH_SHORT).show();

            // Redirect to login screen if the user is not authenticated
            Intent intent = new Intent(ChatActivity.this, Login_Screen.class);
            startActivity(intent);
            finish(); // Optional: Close the current activity to prevent user from returning to the chat screen
        }
    }

    private void fetchGroupsData() {
        // Code to fetch groups data from Firebase
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups");

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle data snapshot
                // Parse and display the groups
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e("ChatActivity", "Error fetching groups: " + databaseError.getMessage());
            }
        });
    }


}