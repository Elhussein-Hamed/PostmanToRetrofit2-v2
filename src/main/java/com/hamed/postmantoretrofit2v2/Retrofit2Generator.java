package com.hamed.postmantoretrofit2v2;

import com.hamed.postmantoretrofit2v2.forms.JsonDialog;
import com.hamed.postmantoretrofit2v2.forms.listeners.DialogClosedListener;
import com.hamed.postmantoretrofit2v2.forms.listeners.JsonDialogReturnedData;
import com.hamed.postmantoretrofit2v2.forms.listeners.ReturnedData;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class Retrofit2Generator extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getData(PlatformDataKeys.PROJECT);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        JsonDialog jsonDialog = new JsonDialog(project);
        jsonDialog.pack();
        jsonDialog.setTitle(Constants.UIConstants.MAIN_DIALOG_TITLE);
        jsonDialog.setSize(Constants.UIConstants.DIALOG_WIDTH, Constants.UIConstants.DIALOG_HEIGHT);
        jsonDialog.setLocationRelativeTo(null);
        jsonDialog.setOnDialogClosedListener(new DialogClosedListener() {
            @Override
            public void onCancelled() {
                // Nothing to do
            }

            @Override
            public void onUserConfirm(ReturnedData data) {
                if (data instanceof JsonDialogReturnedData) {
                    JsonDialogReturnedData jsonDialogReturnedData = (JsonDialogReturnedData) data;

                    // Parse the Postman collection json text
                    Model model = new Model();
                    Collection collection = model.parsePostman(jsonDialogReturnedData.getCollectionJsonText());

                    // If converting json to a Collection object was successful
                    if (collection != null) {

                        //System.out.println("Collection items: " + collection.getItems());
                        if (!collection.isEmpty()) {
                            assert editor != null;
                            UserSettings userSettings = new UserSettings(project);
                            userSettings.setAutomaticClassGenerationOptions(jsonDialogReturnedData.getAutomaticClassGenerationOptions());
                            model.generateRetrofitCode(project, editor, collection.getItems(),
                                    jsonDialogReturnedData.useDynamicHeaders(),
                                    userSettings,
                                    jsonDialog);
                        }
                        else {
                            System.out.println("Empty collection provided!");
                        }
                    }
                    else if (!jsonDialogReturnedData.getCollectionJsonText().isEmpty()) {

                        // Notify the user that the collection json text is not valid
                        Notification notification = getNotification();
                        notification.notify(project);
                    }
                }
            }
        });

        jsonDialog.setVisible(true);
    }

    @NotNull
    private static Notification getNotification() {
        Notification notification = new Notification("Error Report"
                , "Parsing error"
                , "Failed to parse the postman collection, please check if the postman collection is correct " +
                "or Create an issue in Github"
                , NotificationType.ERROR);

        notification.addAction(NotificationAction.createSimple("Create an issue",
                () -> BrowserUtil.browse("https://github.com/Elhussein-Hamed/PostmanToRetrofit2-v2/issues")));
        return notification;
    }
}
