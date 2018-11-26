package com.nirocca.ornithoservice;


public class Message {
    private String text;
    private String priority;

    public Message(String text, String priority) {
        this.text = text;
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    public String getText() {
        return text;
    }
}