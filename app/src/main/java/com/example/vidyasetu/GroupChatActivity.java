package com.example.vidyasetu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private MessageAdapter messageAdapter;
    private List<Message> messagesList;
    private DatabaseReference messagesRef;
    private String groupId;
    private String currentUserId;
    private TextView groupNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        groupId = getIntent().getStringExtra("groupId");  // Get group ID
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messagesRef = FirebaseDatabase.getInstance().getReference("GroupChats").child(groupId);

        messagesList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
        groupNameTextView = findViewById(R.id.groupName);

        sendButton.setOnClickListener(v -> sendMessage());
        groupNameTextView.setOnClickListener(v -> openGroupDetails());

        loadGroupName();
        loadMessages();
        checkUserMembership();
    }


    private void checkUserMembership() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("members");
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(currentUserId)) {
                    sendButton.setEnabled(true); // User is a member, enable the send button
                } else {
                    sendButton.setEnabled(false); // User is not a member, disable the send button
                    Toast.makeText(GroupChatActivity.this, "You are not a member of this group", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                sendButton.setEnabled(false); // Default to disabled on error
            }
        });
    }
    private void openGroupDetails() {
        if (groupId == null || groupId.isEmpty()) {
            Toast.makeText(this, "Group ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(GroupChatActivity.this, GroupDetailsActivity.class);
        intent.putExtra("groupId", groupId);
        startActivity(intent);
    }

    private void loadGroupName() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String groupName = snapshot.child("groupName").getValue(String.class);
                    groupNameTextView.setText(groupName);  // Set group name dynamically
                } else {
                    groupNameTextView.setText("Group Name");  // Default value if not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                groupNameTextView.setText("Group Name");  // Default value on error
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String senderId = currentUser.getUid();

        // Fetch sender name from Firebase Database (Users node)
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(senderId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String senderName = "Unknown"; // Default name
                if (snapshot.exists()) {
                    if (snapshot.child("name").getValue() != null) {
                        senderName = snapshot.child("name").getValue(String.class);
                        Log.d("GroupChatActivity", "Sender name fetched: " + senderName);
                    } else {
                        Log.d("GroupChatActivity", "Name field is missing.");
                    }
                } else {
                    Log.d("GroupChatActivity", "User not found in Firebase.");
                }

                // Create a message object
                HashMap<String, Object> messageMap = new HashMap<>();
                messageMap.put("senderId", senderId);
                messageMap.put("senderName", senderName); // ✅ Now correctly fetched from DB
                messageMap.put("message", messageText);
                messageMap.put("timestamp", System.currentTimeMillis());

                // Push message to Firebase
                messagesRef.push().setValue(messageMap).addOnSuccessListener(unused -> {
                    messageInput.setText(""); // Clear input after sending
                }).addOnFailureListener(e -> {
                    Toast.makeText(GroupChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupChatActivity.this, "Failed to get sender name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message != null) {
                        messagesList.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messagesList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
