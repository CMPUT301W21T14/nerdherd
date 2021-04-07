package com.example.nerdherd;

/**
 * Reply object, consisting of a string field for its contents
 */
public class Reply {

    private String content;

    /**
     * Constructor for Reply
     * @param content Content of the reply as a String
     */
    public Reply(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
