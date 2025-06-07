package com.example.vidyasetu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
    public static DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("Groups");
}
