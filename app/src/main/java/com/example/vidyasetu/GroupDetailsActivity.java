package com.example.vidyasetu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupDetailsActivity extends AppCompatActivity {
    private RecyclerView membersRecyclerView;
    private Button addMembersButton, exitGroupButton;
    private TextView groupNameTextView;
    private DatabaseReference groupRef, usersRef;
    private List<User> memberList;
    private Set<String> currentMemberIds;
    private UserAdapter userAdapter;
    private String groupId, currentUserId;
    private int membersFetched = 0; // For batch UI update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        membersRecyclerView = findViewById(R.id.membersRecyclerView);
        addMembersButton = findViewById(R.id.addMembersButton);
        exitGroupButton = findViewById(R.id.exitGroupButton);
        groupNameTextView = findViewById(R.id.groupNameTextView);

        groupId = getIntent().getStringExtra("groupId");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        memberList = new ArrayList<>();
        currentMemberIds = new HashSet<>();
        userAdapter = new UserAdapter(memberList, currentMemberIds, false);

        membersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        membersRecyclerView.setAdapter(userAdapter);

        loadGroupName();
        loadGroupMembers();

        addMembersButton.setOnClickListener(v -> showAddMembersDialog());
        exitGroupButton.setOnClickListener(v -> exitGroup());
    }

    private void loadGroupName() {
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String groupName = snapshot.child("groupName").getValue(String.class);
                groupNameTextView.setText(groupName != null ? groupName : "Group Name");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                groupNameTextView.setText("Group Name");
            }
        });
    }

    private void loadGroupMembers() {
        groupRef.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberList.clear();
                currentMemberIds.clear();
                membersFetched = 0;

                for (DataSnapshot memberSnapshot : snapshot.getChildren()) {
                    String userId = memberSnapshot.getKey();
                    Boolean isMember = memberSnapshot.getValue(Boolean.class);

                    if (userId != null && isMember != null && isMember) {
                        currentMemberIds.add(userId);
                        fetchUserDetails(userId);
                    }
                }

                if (currentMemberIds.isEmpty()) {
                    runOnUiThread(() -> userAdapter.notifyDataSetChanged());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GroupDetailsActivity", "Failed to load members: " + error.getMessage());
            }
        });
    }

    private void fetchUserDetails(String userId) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                membersFetched++;
                if (snapshot.exists()) {
                    String userName = snapshot.child("name").getValue(String.class);
                    if (userName != null && currentMemberIds.contains(userId)) {
                        memberList.add(new User(userId, userName));
                    }
                }
                if (membersFetched == currentMemberIds.size()) {
                    runOnUiThread(() -> userAdapter.notifyDataSetChanged());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                membersFetched++;
                if (membersFetched == currentMemberIds.size()) {
                    runOnUiThread(() -> userAdapter.notifyDataSetChanged());
                }
            }
        });
    }

    private void showAddMembersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_members, null);
        builder.setView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerAddMembers);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);

        Set<String> selectedUserIds = new HashSet<>();
        List<User> nonMemberList = new ArrayList<>();
        UserAdapter userAdapter = new UserAdapter(nonMemberList, selectedUserIds, true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nonMemberList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String userName = userSnapshot.child("name").getValue(String.class);

                    if (userId != null && userName != null && !currentMemberIds.contains(userId)) {
                        nonMemberList.add(new User(userId, userName));
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("GroupDetailsActivity", "Failed to fetch users: " + error.getMessage());
            }
        });

        btnCancel.setOnClickListener(v -> {
            if (dialog.isShowing()) dialog.dismiss();
        });

        btnAdd.setOnClickListener(v -> {
            if (!selectedUserIds.isEmpty()) {
                addMembersToGroup(selectedUserIds);
                if (dialog.isShowing()) dialog.dismiss();
            } else {
                Toast.makeText(GroupDetailsActivity.this, "Select at least one member", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMembersToGroup(Set<String> newMembers) {
        DatabaseReference membersRef = groupRef.child("members");

        for (String userId : newMembers) {
            membersRef.child(userId).setValue(true)
                    .addOnSuccessListener(aVoid -> Log.d("GroupDetailsActivity", "User " + userId + " added"))
                    .addOnFailureListener(e -> Log.e("GroupDetailsActivity", "Failed to add user " + userId + ": " + e.getMessage()));
        }

        Toast.makeText(this, "Members Added Successfully!", Toast.LENGTH_SHORT).show();
        loadGroupMembers();
    }

    private void exitGroup() {
        groupRef.child("members").child(currentUserId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(GroupDetailsActivity.this, "You exited the group", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(GroupDetailsActivity.this, "Failed to exit group", Toast.LENGTH_SHORT).show();
                    Log.e("GroupDetailsActivity", "Error exiting group: " + e.getMessage());
                });
    }
}
