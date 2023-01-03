package com.hamed.postmantoretrofit2v2.pluginstate.helperclasses;

@SuppressWarnings("unused")
public class JavaAutomaticClassGenerationCommonOptions implements AutomaticClassGenerationOptions {

    private boolean useJavaPrimitiveOptions;

    public JavaAutomaticClassGenerationCommonOptions() {
        this.useJavaPrimitiveOptions = false;
    }

    public boolean isUseJavaPrimitiveOptions() {
        return useJavaPrimitiveOptions;
    }

    public void setUseJavaPrimitiveOptions(boolean useJavaPrimitiveOptions) {
        this.useJavaPrimitiveOptions = useJavaPrimitiveOptions;
    }
}
