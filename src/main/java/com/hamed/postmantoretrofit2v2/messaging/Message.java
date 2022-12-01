package com.hamed.postmantoretrofit2v2.messaging;

public abstract class Message {

    private final Object object;

    public Message(Object object) {
        this.object = object;
    }

    public Object getContent() {
        return object;
    }
}
