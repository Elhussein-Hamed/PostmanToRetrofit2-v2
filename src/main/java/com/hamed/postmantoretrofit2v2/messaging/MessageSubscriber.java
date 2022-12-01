package com.hamed.postmantoretrofit2v2.messaging;

public interface MessageSubscriber {
    void onMessageReceived(Message message);
}
