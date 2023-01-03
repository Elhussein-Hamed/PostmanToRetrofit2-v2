package com.hamed.postmantoretrofit2v2.utils;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class DependencyPluginHelper {
    
    public static boolean isPluginUsable(Project project, String pluginName, String pluginId)
    {
        if (PluginManagerCore.isDisabled(PluginId.getId(pluginId)))
        {
            System.out.printf("%s plugin is disabled\n", pluginName);
            boolean doEnable = Messages.showYesNoDialog(
                    String.format("The %s plugin used to generate the classes is disabled, " +
                            "would you like to enable it? Requires IDE restart.", pluginName),
                    String.format("Enable %s Plugin?", pluginName),
                    "Enable",
                    "Keep Disabled",
                    Messages.getQuestionIcon()
            ) == Messages.YES;

            if (doEnable) {
                PluginManager.getInstance().enablePlugin(PluginId.getId(pluginId));
                System.out.printf("Enabled %s plugin\n", pluginName);
                ProjectUtils.restartIde();
            }
            else
                return false;
        }
        else if (!PluginManagerCore.isPluginInstalled(PluginId.getId(pluginId)))
        {
            System.out.printf("%s plugin is not installed\n", pluginName);
            Messages.showMessageDialog(project, String.format("Error %s plugin is not installed, please install it from the market place", pluginName), "Dependency Plugin Missing", Messages.getWarningIcon());
            return false;
        }
        
        return true;
    }
}
