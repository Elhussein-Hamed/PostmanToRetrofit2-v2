package com.hamed.postmantoretrofit2v2;

import com.hamed.postmantoretrofit2v2.forms.JsonDialog;
import com.hamed.postmantoretrofit2v2.forms.listeners.DialogClosedListener;
import com.hamed.postmantoretrofit2v2.forms.listeners.JsonDialogReturnedData;
import com.hamed.postmantoretrofit2v2.forms.listeners.ReturnedData;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.Objects;

public class Retrofit2Generator extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getData(PlatformDataKeys.PROJECT);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        Model model = new Model(project, editor);

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
            @SuppressWarnings("deprecation")
            public void onUserConfirm(ReturnedData data) {
                if (data instanceof JsonDialogReturnedData) {
                    JsonDialogReturnedData jsonDialogReturnedData = (JsonDialogReturnedData) data;

                    // Parse the Postman collection json text
                    Collection collection = model.parsePostman(jsonDialogReturnedData.getCollectionJsonText());

                    // If converting json to a Collection object was successful
                    if (collection != null) {

                        // Get the return type from the stored plugin state
                        PluginState state = PluginService.getInstance(Objects.requireNonNull(project)).getState();
                        assert state != null;

                        System.out.println("Selected return type: " + state.getReturnType());
                        model.generateRetrofitCode(collection.getItems(),
                                jsonDialogReturnedData.useDynamicHeaders(),
                                state.getReturnType(),
                                jsonDialog);
                    }
                    else if (!jsonDialogReturnedData.getCollectionJsonText().isEmpty()) {

                        // Notify the user that the collection json text is not valid
                        Notification notification = new Notification("Error Report"
                                , "Parsing error"
                                , "Failed to parse the postman collection, please check if the postman collection is correct " +
                                "or Create an issue " +
                                " <a href=\"https://github.com/Elhussein-Hamed/PostmanToRetrofit2-v2/issues\">here</a>"
                                , NotificationType.ERROR);

                        notification.setListener(NotificationListener.URL_OPENING_LISTENER);
                        notification.notify(project);
                    }
                }
            }
        });

        jsonDialog.setVisible(true);
    }
}
