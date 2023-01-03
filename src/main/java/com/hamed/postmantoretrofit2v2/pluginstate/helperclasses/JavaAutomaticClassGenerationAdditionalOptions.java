package com.hamed.postmantoretrofit2v2.pluginstate.helperclasses;

@SuppressWarnings("unused")
public class JavaAutomaticClassGenerationAdditionalOptions extends JavaAutomaticClassGenerationCommonOptions {
    private boolean createGetters;
    private boolean createSetters;
    private boolean overrideToString;

    public JavaAutomaticClassGenerationAdditionalOptions() {
        super();
        this.createGetters = false;
        this.createSetters = false;
        this.overrideToString = false;
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
}
