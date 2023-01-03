package com.hamed.postmantoretrofit2v2.pluginstate.helperclasses;

@SuppressWarnings("unused")
public class AutomaticClassGenerationOptions {
    private boolean useJavaPrimitiveOptions;
    private boolean createGetters;
    private boolean createSetters;
    private boolean overrideToString;

    // Use @Value
    private boolean useAtValue;
    private boolean useDataClasses;
    private boolean singleFile;
    private boolean nullableFields;
    private boolean parcelableAndroid;

    private boolean generateAdapter;

    public AutomaticClassGenerationOptions() {
        this.useJavaPrimitiveOptions = false;
        this.createGetters = false;
        this.createSetters = false;
        this.overrideToString = false;
        this.useAtValue = false;
        this.useDataClasses = false;
        this.singleFile = false;
        this.nullableFields = false;
        this.parcelableAndroid = false;
        this.generateAdapter = false;
    }

    public boolean isUseJavaPrimitiveOptions() {
        return useJavaPrimitiveOptions;
    }

    public void setUseJavaPrimitiveOptions(boolean useJavaPrimitiveOptions) {
        this.useJavaPrimitiveOptions = useJavaPrimitiveOptions;
    }

    public boolean isCreateGetters() {
        return createGetters;
    }

    public void setCreateGetters(boolean createGetters) {
        this.createGetters = createGetters;
    }

    public boolean isCreateSetters() {
        return createSetters;
    }

    public void setCreateSetters(boolean createSetters) {
        this.createSetters = createSetters;
    }

    public boolean isOverrideToString() {
        return overrideToString;
    }

    public void setOverrideToString(boolean overrideToString) {
        this.overrideToString = overrideToString;
    }

    public boolean isUseAtValue() {
        return useAtValue;
    }

    public void setUseAtValue(boolean useAtValue) {
        this.useAtValue = useAtValue;
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

    public boolean isGenerateAdapter() {
        return generateAdapter;
    }

    public void setGenerateAdapter(boolean generateAdapter) {
        this.generateAdapter = generateAdapter;
    }

    @Override
    public String toString() {
        return "AutomaticClassGenerationOptions2{" +
                "useJavaPrimitiveOptions=" + useJavaPrimitiveOptions +
                ", createGetters=" + createGetters +
                ", createSetters=" + createSetters +
                ", overrideToString=" + overrideToString +
                ", useAtValue=" + useAtValue +
                ", useDataClasses=" + useDataClasses +
                ", singleFile=" + singleFile +
                ", nullableFields=" + nullableFields +
                ", parcelableAndroid=" + parcelableAndroid +
                ", generateAdapter=" + generateAdapter +
                '}';
    }
}
