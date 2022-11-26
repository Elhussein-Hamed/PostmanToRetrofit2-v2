package com.hamed.postmantoretrofit2v2.errorreporting;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.NlsActions;
import com.intellij.util.Consumer;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class PluginErrorReportSubmitter extends ErrorReportSubmitter {

    private static final String URL = "https://elhussein-hamed.atlassian.net/rest/collectors/1.0/template/form/d140a2ad";

    @Override
    public @NlsActions.ActionText @NotNull String getReportActionText() {
        return "Submit to Elhussein Hamed";
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {
        final DataContext dataContext = DataManager.getInstance().getDataContext(parentComponent);
        final Project project = CommonDataKeys.PROJECT.getData(dataContext);

        StringBuilder stacktrace = new StringBuilder();
        for (IdeaLoggingEvent event : events) {
            stacktrace.append(event.getMessage()).append("\n");
            stacktrace.append(event.getThrowableText()).append("\n");
        }

        String description = prepareDescription(stacktrace.toString());

        Task.Backgroundable task = new Task.Backgroundable(project, "PostmanToRetrofit2 V2 error submission", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("Submitting PostmanToRetrofit2 V2 error report...");
                indicator.setIndeterminate(true);

                String content = "description=" + description +
                        "&email=" +
                        "&fullname=PostmanToRetrofit2 V2 plugin error reporter" +
                        "&webInfo=IDEA build: " + ApplicationInfo.getInstance().getBuild().asString();

                try {

                    OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();

                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(content, mediaType);
                    Request request = new Request.Builder()
                            .url(URL)
                            .method("POST", body)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        System.out.println("Successfully submitted the error report");
                        consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE));

                        ApplicationManager.getApplication().invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Messages.showInfoMessage(parentComponent, "The error report has been submitted successfully. Thank you for your feedback!", "Postman To Retrofit2 V2 Error Submission");
                            }
                        });
                    } else {
                        System.out.println("Failed to submit the error report");
                        consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.FAILED));
                    }

                    response.close();
                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Messages.showWarningDialog(parentComponent, "Failed to submit the error report, please check your internet connection.", "Postman To Retrofit2 V2 Error Submission");
                            consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.FAILED));
                        }
                    });
                }
            }
        };

        BackgroundableProcessIndicator indicator = new BackgroundableProcessIndicator(task);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, indicator);
        return true;
    }

    private String prepareDescription(String stacktrace) {
        return "PluginId: " + getPluginDescriptor().getPluginId() + "\n" +
                "Plugin name: " + getPluginDescriptor().getName() + "\n" +
                "Plugin version: " + getPluginDescriptor().getVersion() + "\n" +
                "IDEA build: " + ApplicationInfo.getInstance().getBuild().asString() + "\n\n" +
                "Stacktrace: \n" + stacktrace + "\n";
    }
}
