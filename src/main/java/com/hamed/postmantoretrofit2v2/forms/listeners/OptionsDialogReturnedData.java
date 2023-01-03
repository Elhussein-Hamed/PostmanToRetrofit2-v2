package com.hamed.postmantoretrofit2v2.forms.listeners;

import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.AutomaticClassGenerationOptions;

/**
 * Represents the data to be returned from the Options Dialog when it is closed.
 * Only the data that is not stored in the Plugin state needs to be returned.
 * For example: The framework chosen by the user doesn't need to be returned
 * because it will be stored in the Plugin state, on the other hand
 * the class generation options should be returned because it will not be stored
 * in the plugin state.
 */
public class OptionsDialogReturnedData implements ReturnedData {
    private final AutomaticClassGenerationOptions automaticClassGenerationOptions;

    public OptionsDialogReturnedData(AutomaticClassGenerationOptions automaticClassGenerationOptions) {
        this.automaticClassGenerationOptions = automaticClassGenerationOptions;
    }

    public AutomaticClassGenerationOptions getAutomaticClassGenerationOptions() {
        return automaticClassGenerationOptions;
    }
}
