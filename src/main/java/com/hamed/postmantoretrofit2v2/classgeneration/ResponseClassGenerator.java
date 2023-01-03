package com.hamed.postmantoretrofit2v2.classgeneration;

import com.hamed.postmantoretrofit2v2.utils.DependencyPluginHelper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.robohorse.robopojogenerator.models.FrameworkVW;
import com.robohorse.robopojogenerator.models.GenerationModel;
import com.robohorse.robopojogenerator.models.ProjectModel;
import org.json.JSONException;

public class ResponseClassGenerator {

    public static boolean generateClasses(Project project, String directory, String name, String responseBody)
    {
        // Response body example:
        // "{\n    \"userId\": 1,\n    \"id\": 1,\n    \"title\": \"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\n    \"body\": \"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"\n}"
        //responseBody = "{\n    \"userId\": 1,\n    \"id\": 1,\n    \"title\": \"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\n    \"body\": \"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"\n}";

        if (!DependencyPluginHelper.isPluginUsable(project, "RoboPojoGenerator", "com.robohorse.robopojogenerator"))
            return false;

        try {
            // Prepare the generation model
            GenerationModel generationModel = new GenerationModel(false,
                    false,
                    new FrameworkVW.Gson(),
                    name,
                    responseBody,
                    true,
                    true,
                    false,
                    false,
                    false,
                    false,
                    true,
                    true,
                    false,
                    false,
                    false
                    );

            // Prepare the project module
            VirtualFile returnTypeClassesDirVirtualFile = LocalFileSystem.getInstance().findFileByPath(directory);
            assert returnTypeClassesDirVirtualFile != null;
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(returnTypeClassesDirVirtualFile);
            assert psiDirectory != null;

            String packageName = ProjectRootManager.getInstance(project).getFileIndex().getPackageNameByDirectory(returnTypeClassesDirVirtualFile);

            ProjectModel projectModel = new ProjectModel(
                    psiDirectory,
                    packageName,
                    returnTypeClassesDirVirtualFile,
                    project
            );

            try {
                new GenerationApp().generate(generationModel, projectModel);
            } catch (JSONException e) {
                String message = String.format(
                        "Couldn't parse json due to the following error:\n<b>%s</b>\n The provided Json is:\n <b>%s</b>", e.getMessage(), responseBody);
                System.out.println(message);
                Messages.showMessageDialog(project,
                        message,
                        "Parsing Json Failure", Messages.getWarningIcon());
            }

        } catch (NoClassDefFoundError ex) {
            // Restart the IDE
            Messages.showMessageDialog(project, "Error calling RoboPojoGenerator plugin, please restart the IDE", "Plugin Requires Restart", Messages.getWarningIcon());
        }

        return true;
    }
}

