package com.hamed.postmantoretrofit2v2.messaging;

import com.hamed.postmantoretrofit2v2.utils.ClassInfo;

public class NewClassInfoAddedMessage extends Message {

    public NewClassInfoAddedMessage(ClassInfo classInfo) {
        super(classInfo);
    }
}
