package com.example.vidyasetu;

public class User {
     String id;
     String name;  // Corrected from 'username' to 'name'
     String email;
     String lastMessage;

    public User() {
        // Empty constructor required for Firebase
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter Methods
    public String getId() {
        return id;
    }

    public String getName() {  // Fixed: getUsername -> getName
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    // Setter Methods
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {  // Fixed: setUsername -> setName
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
