package com.example.vidyasetu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CreateGroupActivity extends AppCompatActivity {

    private RecyclerView userRecycler;
    private FloatingActionButton createGroupBtn;
    private DatabaseReference usersRef, groupsRef;
    private List<User> userList;
    private UserAdapter userAdapter;
    private Set<String> selectedUserIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userRecycler = findViewById(R.id.userRecycler);
        createGroupBtn = findViewById(R.id.createGroupBtn);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

        userList = new ArrayList<>();
        selectedUserIds = new HashSet<>();
        userAdapter = new UserAdapter(userList, selectedUserIds, true);
        userRecycler.setLayoutManager(new LinearLayoutManager(this));
        userRecycler.setAdapter(userAdapter);

        loadUsers();

        createGroupBtn.setOnClickListener(v -> createGroup());
    }

    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                Log.d("FirebaseUser", "Loading users...");

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String storedUserId = userSnapshot.child("id").getValue(String.class);
                    String name = userSnapshot.child("name").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);

                    if (storedUserId != null && name != null && email != null) {
                        Log.d("FirebaseUser", "User found: " + name + " (" + email + ")");

                        if (!email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                            User user = new User(storedUserId, name);
                            userList.add(user);
                        }
                    }
                }

                runOnUiThread(() -> {
                    if (userList.isEmpty()) {
                        Log.d("FirebaseUser", "No users found.");
                        Toast.makeText(CreateGroupActivity.this, "No users found", Toast.LENGTH_SHORT).show();
                    }
                    userAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseUser", "Failed to load users: " + error.getMessage());
                Toast.makeText(CreateGroupActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createGroup() {
        if (selectedUserIds.isEmpty()) {
            Toast.makeText(this, "Select at least one user", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the dialog with custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customDialogView = getLayoutInflater().inflate(R.layout.dialog_create_group, null);
        builder.setView(customDialogView);

        EditText groupNameInput = customDialogView.findViewById(R.id.groupNameInput);
        Button cancelButton = customDialogView.findViewById(R.id.cancelButton);
        Button okButton = customDialogView.findViewById(R.id.okButton);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle Cancel button click
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Handle OK button click
        okButton.setOnClickListener(v -> {
            String groupName = groupNameInput.getText().toString().trim();
            if (groupName.isEmpty()) {
                Toast.makeText(this, "Group name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add current user to the selected members
            selectedUserIds.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

            String groupId = UUID.randomUUID().toString(); // Generate a unique group ID

            // Convert selectedUserIds to a Map (Key-Value Pair)
            Map<String, Boolean> membersMap = new HashMap<>();
            for (String userId : selectedUserIds) {
                membersMap.put(userId, true);
            }

            Group group = new Group(groupId, groupName, membersMap); // Store members as key-value pairs

            groupsRef.child(groupId).setValue(group)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Group Created", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Group creation failed", Toast.LENGTH_SHORT).show();
                        }
                    });

            dialog.dismiss();
        });
    }
}
