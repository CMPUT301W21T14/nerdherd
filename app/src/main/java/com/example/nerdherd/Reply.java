package com.example.nerdherd;

/**
 * Reply object, consisting of a string field for its contents
 */
public class Reply {

    private String content;

    public Reply() {

    }

    public Reply(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
