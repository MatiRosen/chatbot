package io.github.matirosen.chatbot;

import java.util.ArrayList;
import java.util.List;

public class BotMessage {

    private final String key;
    private final List<String> messages = new ArrayList<>(), permissionResponses = new ArrayList<>(), noPermissionResponses = new ArrayList<>();
    private String permission = "";

    public BotMessage(String key){
        this.key = key;
    }

    public void addMessage(String message){
        this.messages.add(message);
    }

    public void addNoPermissionResponse(String response){
        this.noPermissionResponses.add(response);
    }

    public void addPermissionResponse(String response){
        this.permissionResponses.add(response);
    }

    public void addPermission(String permission){
        this.permission = permission;
    }

    public String getKey(){
        return key;
    }

    public List<String> getMessages(){
        return messages;
    }

    public List<String> getPermissionResponses(){
        return permissionResponses;
    }

    public List<String> getNoPermissionResponses(){
        return noPermissionResponses;
    }

    public String getPermission(){
        return permission;
    }
}
