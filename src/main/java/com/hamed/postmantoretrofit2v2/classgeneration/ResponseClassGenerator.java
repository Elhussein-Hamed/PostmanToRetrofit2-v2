package com.hamed.postmantoretrofit2v2.classgeneration;

import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.AutomaticClassGenerationOptions;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.Framework;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.Language;
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

    public static boolean generateClasses(Project project, String directory, String name, String responseBody, Language language, Framework framework, AutomaticClassGenerationOptions generationOptions)
    {
        // Response body example:
        // "{\n    \"userId\": 1,\n    \"id\": 1,\n    \"title\": \"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\n    \"body\": \"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"\n}"
        //responseBody = "{\n    \"userId\": 1,\n    \"id\": 1,\n    \"title\": \"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\n    \"body\": \"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"\n}";

        try {
            // Prepare the generation model
            GenerationModel generationModel = new GenerationModel(
                    false,
                    language == Language.KOTLIN,
                    getFrameworkVW(framework),
                    name,
                    responseBody,
                    generationOptions.isCreateSetters(),
                    generationOptions.isCreateGetters(),
                    generationOptions.isOverrideToString(),
                    generationOptions.isSingleFile(),
                    generationOptions.isParcelableAndroid(),
                    generationOptions.isNullableFields(),
                    generationOptions.isUseJavaPrimitiveOptions(),
                    true,
                    generationOptions.isUseAtValue(),
                    generationOptions.isGenerateAdapter(),
                    generationOptions.isUseDataClasses()
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

            return true;

        } catch (NoClassDefFoundError ex) {
            // Restart the IDE
            Messages.showMessageDialog(project, "Error calling RoboPojoGenerator plugin, please restart the IDE", "Plugin Requires Restart", Messages.getWarningIcon());
        }

        return false;
    }
    
    private static FrameworkVW getFrameworkVW(Framework framework)
    {
        if (Framework.NONE == framework) {
            return new FrameworkVW.None();
        }
        else if (Framework.NONE_RECORDS == framework) {
            return new FrameworkVW.NoneJavaRecords();
        }
        else if (Framework.LOMBOK == framework) {
            return new FrameworkVW.NoneLombok();
        }
        else if (Framework.GSON == framework) {
            return new FrameworkVW.Gson();
        }
        else if (Framework.GSON_RECORDS == framework) {
            return new FrameworkVW.GsonJavaRecords();
        }
        else if (Framework.JACKSON == framework) {
            return new FrameworkVW.Jackson();
        }
        else if (Framework.JACKSON_RECORDS == framework) {
            return new FrameworkVW.JacksonJavaRecords();
        }
        else if (Framework.LOGAN_SQUARE == framework) {
            return new FrameworkVW.LoganSquare();
        }
        else if (Framework.LOGAN_SQUARE_RECORDS == framework) {
            return new FrameworkVW.LoganSquareJavaRecords();
        }
        else if (Framework.MOSHI == framework) {
            return new FrameworkVW.Moshi();
        }
        else if (Framework.MOSHI_RECORDS == framework) {
            return new FrameworkVW.MoshiJavaRecords();
        }
        else if (Framework.FASTJSON == framework) {
            return new FrameworkVW.FastJson();
        }
        else if (Framework.FASTJSON_RECORDS == framework) {
            return new FrameworkVW.FastJsonJavaRecords();
        }
        else if (Framework.AUTO_VALUE == framework) {
            return new FrameworkVW.AutoValue();
        }
        else
            return new FrameworkVW.None();
    }
}

