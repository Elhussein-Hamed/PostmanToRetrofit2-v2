package com.hamed.postmantoretrofit2v2.pluginstate.helperclasses;

@SuppressWarnings("unused")
public class JavaAutomaticClassGenerationLombokOptions extends JavaAutomaticClassGenerationCommonOptions {

    // Use @ value
    private boolean useAtValue;
    public JavaAutomaticClassGenerationLombokOptions() {
        super();
        this.useAtValue = false;
    }

    public boolean isUseAtValue() {
        return useAtValue;
    }

    public void setUseAtValue(boolean useAtValue) {
        this.useAtValue = useAtValue;
    }
}
