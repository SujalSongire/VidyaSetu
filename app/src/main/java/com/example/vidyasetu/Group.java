package com.example.vidyasetu;

import java.util.HashMap;
import java.util.Map;

public class Group {
    public String groupId, groupName;
    public Map<String, Boolean> members;

    public Group() {}

    public Group(String groupId, String groupName, Map<String, Boolean> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.members = members;
    }
}
