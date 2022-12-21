package com.hamed.postmantoretrofit2v2.forms.listeners;

public class JsonDialogReturnedData implements ReturnedData {
    private final String collectionJsonText;

    private final boolean useDynamicHeaders;

    public JsonDialogReturnedData(String collectionJsonText, boolean useDynamicHeaders) {
        this.collectionJsonText = collectionJsonText;
        this.useDynamicHeaders = useDynamicHeaders;
    }

    public String getCollectionJsonText() {
        return collectionJsonText;
    }

    public boolean useDynamicHeaders() {
        return useDynamicHeaders;
    }

}
