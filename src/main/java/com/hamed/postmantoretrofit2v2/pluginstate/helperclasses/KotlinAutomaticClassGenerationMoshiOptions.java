package com.hamed.postmantoretrofit2v2.pluginstate.helperclasses;

@SuppressWarnings("unused")
public class KotlinAutomaticClassGenerationMoshiOptions extends KotlinAutomaticClassGenerationCommonOptions{

    private boolean generateAdapter;

    public KotlinAutomaticClassGenerationMoshiOptions() {
        super();
        this.generateAdapter = false;
    }

    public boolean isGenerateAdapter() {
        return generateAdapter;
    }

    public void setGenerateAdapter(boolean generateAdapter) {
        this.generateAdapter = generateAdapter;
    }
}
