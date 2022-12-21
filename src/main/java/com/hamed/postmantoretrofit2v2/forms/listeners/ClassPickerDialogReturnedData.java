package com.hamed.postmantoretrofit2v2.forms.listeners;

public class ClassPickerDialogReturnedData implements ReturnedData {
    private final String modifiedRetrofitAnnotatedMethod;
    public ClassPickerDialogReturnedData(String modifiedRetrofitAnnotatedMethod) {
        this.modifiedRetrofitAnnotatedMethod = modifiedRetrofitAnnotatedMethod;
    }

    public String getModifiedRetrofitAnnotatedMethod() {
        return modifiedRetrofitAnnotatedMethod;
    }

}
