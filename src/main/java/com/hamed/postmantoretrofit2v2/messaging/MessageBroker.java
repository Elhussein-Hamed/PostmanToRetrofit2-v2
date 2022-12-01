package com.hamed.postmantoretrofit2v2.messaging;

import java.util.ArrayList;

public class MessageBroker {

    private static MessageBroker messageBroker = null;
    private final ArrayList<MessageSubscriber> subscribers;


    private MessageBroker() {
        subscribers = new ArrayList<>();
    }

    public static MessageBroker getInstance()
    {
        if (messageBroker == null)
            messageBroker = new MessageBroker();

        return messageBroker;
    }

    public void sendMessage(Message message)
    {
        for (MessageSubscriber subscriber: subscribers)
            subscriber.onMessageReceived(message);
    }

    public void addSubscriber(MessageSubscriber subscriber)
    {
        subscribers.add(subscriber);
    }

    public void removeSubscriber(MessageSubscriber subscriber)
    {
        for (MessageSubscriber sub: subscribers) {
            if (sub == subscriber) {
                subscribers.remove(sub);
                break;
            }
        }
    }
}
