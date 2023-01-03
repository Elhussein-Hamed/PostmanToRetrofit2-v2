package com.hamed.postmantoretrofit2v2;

import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.Language;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.ReturnTypeRadioButton;
import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;

import java.util.Objects;

/**
 * Access the user settings that are saved in the plugin state.
 * No actual settings are saved in this class, instead they are being retrieved
 * from the plugin state whenever a function is called to make sure the
 * latest state is returned.
 * The main objective of this class is to decouple the plugin state from the Model class.
 */
public class UserSettings {

    private final Project mProject;

    public UserSettings(Project project) {
        this.mProject = project;
    }

    public int getIndentSize() {
        Objects.requireNonNull(mProject, "'mProject should be initialised before calling this method");
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        // The language might change after the class initialisation. To count for this
        // the indent size will be checked again in generateRxJavaCode function
        CodeStyleSettings styleSettings = CodeStyle.getProjectOrDefaultSettings(mProject);

        FileType fileType;
        if (state.getLanguage() == Language.JAVA)
            fileType = FileTypeManager.getInstance().findFileTypeByName("JAVA");
        else // Kotlin
            fileType = FileTypeManager.getInstance().findFileTypeByName("Kotlin");

        CommonCodeStyleSettings.IndentOptions options = styleSettings.getIndentOptions(fileType);
        int indentSize = options.INDENT_SIZE;
        System.out.println("Indent size: " + indentSize);
        return indentSize;
    }

    public Language getLanguage()
    {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;
        return state.getLanguage();
    }

    public boolean useCoroutines()
    {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;
        return state.getReturnTypeRadioButton() == ReturnTypeRadioButton.BUTTON_RETROFIT_AND_COROUTINES;
    }

    public boolean promptToSelectClassForReturnType()
    {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;
        return state.getPromptToSelectClassForReturnType();
    }

    public String getReturnType()
    {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;
        return state.getReturnType();
    }

    public String getReturnTypeClassesDirectory()
    {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;
        return state.getReturnTypeClassesDirectory();
    }
}
