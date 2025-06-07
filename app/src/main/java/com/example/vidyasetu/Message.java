
package com.example.vidyasetu;
public class Message {
    private String senderId;
    private String senderName;
    private String message;
    private long timestamp;

    public Message() { } // Empty constructor for Firebase

    public Message(String senderId, String senderName, String message, long timestamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
}
