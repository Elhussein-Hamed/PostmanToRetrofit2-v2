package com.hamed.postmantoretrofit2v2.pluginstate.helperclasses;

@SuppressWarnings("unused")
public class KotlinAutomaticClassGenerationCommonOptions implements AutomaticClassGenerationOptions {

    private boolean useDataClasses;
    private boolean singleFile;
    private boolean nullableFields;
    private boolean parcelableAndroid;

    public KotlinAutomaticClassGenerationCommonOptions() {
        this.useDataClasses = false;
        this.singleFile = false;
        this.nullableFields = false;
        this.parcelableAndroid = false;
    }

    public boolean isUseDataClasses() {
        return useDataClasses;
    }

    public void setUseDataClasses(boolean useDataClasses) {
        this.useDataClasses = useDataClasses;
    }

    public boolean isSingleFile() {
        return singleFile;
    }

    public void setSingleFile(boolean singleFile) {
        this.singleFile = singleFile;
    }

    public boolean isNullableFields() {
        return nullableFields;
    }

    public void setNullableFields(boolean nullableFields) {
        this.nullableFields = nullableFields;
    }

    public boolean isParcelableAndroid() {
        return parcelableAndroid;
    }

    public void setParcelableAndroid(boolean parcelableAndroid) {
        this.parcelableAndroid = parcelableAndroid;
    }
}
