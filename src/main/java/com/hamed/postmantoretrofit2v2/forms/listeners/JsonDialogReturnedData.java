package com.hamed.postmantoretrofit2v2.forms.listeners;

import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.AutomaticClassGenerationOptions;

public class JsonDialogReturnedData implements ReturnedData {
    private final String collectionJsonText;

    private final boolean useDynamicHeaders;

    private final AutomaticClassGenerationOptions automaticClassGenerationOptions;

    public JsonDialogReturnedData(String collectionJsonText, boolean useDynamicHeaders, AutomaticClassGenerationOptions automaticClassGenerationOptions) {
        this.collectionJsonText = collectionJsonText;
        this.useDynamicHeaders = useDynamicHeaders;
        this.automaticClassGenerationOptions = automaticClassGenerationOptions;
    }

    public String getCollectionJsonText() {
        return collectionJsonText;
    }

    public boolean useDynamicHeaders() {
        return useDynamicHeaders;
    }

    public AutomaticClassGenerationOptions getAutomaticClassGenerationOptions() {
        return automaticClassGenerationOptions;
    }
}
