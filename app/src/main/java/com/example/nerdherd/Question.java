package com.example.nerdherd;

import java.util.ArrayList;

public class Question {

    private String content;
    private ArrayList<Reply> replies = new ArrayList<Reply>();
    private int numberOfReplies = 0;

    public Question(String content) {
        this.content = content;
    }

    public ArrayList<Reply> getReplies() {
        return replies;
    }
    public void addReply(Reply reply) {
        this.replies.add(reply);
    }

    public int getNumberOfReplies() { return this.numberOfReplies; }
    public void incrementReplies() {
        numberOfReplies += 1;
    }

    public String getContent() {
        return content;
    }
}
