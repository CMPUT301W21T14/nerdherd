package com.example.nerdherd;

import java.util.ArrayList;

public class Question {

    private String content;
    private ArrayList<Reply> replies = new ArrayList<Reply>();

    public Question() {

    }

    public Question(String content) {
        this.content = content;
    }

    public ArrayList<Reply> getReplies() {
        if( replies == null ) {
            replies = new ArrayList<>();
        }
        return replies;
    }
    public void addReply(Reply reply) {
        this.replies.add(reply);
    }

    public String getContent() {
        return content;
    }
}
