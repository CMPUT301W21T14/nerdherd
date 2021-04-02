package com.example.nerdherd;

public class Reply {

    private String content;
    private String status;

    public Reply(String content, String status) {
        this.content = content;
        this.status = status;
    }

    public String getContent() {
        return content;
    }
    public String getStatus() {
        return status;
    }
}
