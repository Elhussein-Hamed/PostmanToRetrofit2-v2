package com.hamed.postmantoretrofit2v2;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hamed.postmantoretrofit2v2.Collection.Item;
import com.hamed.postmantoretrofit2v2.forms.ClassPickerDialog;
import com.hamed.postmantoretrofit2v2.forms.listeners.ClassPickerDialogReturnedData;
import com.hamed.postmantoretrofit2v2.forms.listeners.DialogClosedListener;
import com.hamed.postmantoretrofit2v2.forms.listeners.ReturnedData;
import com.hamed.postmantoretrofit2v2.pluginstate.Language;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginService;
import com.hamed.postmantoretrofit2v2.pluginstate.PluginState;
import com.hamed.postmantoretrofit2v2.pluginstate.ReturnTypeRadioButton;
import com.hamed.postmantoretrofit2v2.utils.RetrofitSyntaxHelper;
import com.hamed.postmantoretrofit2v2.utils.Utils;
import com.intellij.application.options.CodeStyle;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;

import javax.annotation.Nullable;
import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Model {

    public static final String RETURN_TYPE_POSTFIX = "Response";
    private int INDENT_SIZE;

    private final Project mProject;
    private final Editor mEditor;

    public Model(Project project, Editor editor) {
        mProject = project;
        mEditor = editor;
        INDENT_SIZE = 4;

        updateIndentSize();
    }

    private void updateIndentSize() {
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
        INDENT_SIZE = options.INDENT_SIZE;
        System.out.println("Indent size: " + INDENT_SIZE);
    }

    @Nullable
    public Collection parsePostman(String jsonString) {
        try{
            Collection collection = new Gson().fromJson(jsonString, Collection.class);
            System.out.println(collection);
            if (collection != null && !collection.isValid())
                throw new Exception("Invalid collection");
            return collection;
        } catch (Exception e){
            System.out.println("Json parse failed due to: " + e);
        }
        return null;
    }

    public void generateRetrofitCode(List<Item> items, boolean isDynamicHeader, String returnTypeFormat, JDialog parentDialog) {

        updateIndentSize();
        int lastCaretPosition = mEditor.getCaretModel().getOffset();

        returnTypeFormat = Utils.highlightReturnTypeWithHashes(returnTypeFormat);
        ArrayList<String> retrofitAnnotatedMethods = constructRetrofitAnnotatedMethods(items, isDynamicHeader, returnTypeFormat);

        for (int i = 0; i < retrofitAnnotatedMethods.size(); i++) {

            PluginState state = PluginService.getInstance(mProject).getState();
            if (Objects.requireNonNull(state).getPromptToSelectClassForReturnType()) {

                ClassPickerDialog classPickerDialog = new ClassPickerDialog(parentDialog, mProject, mEditor, retrofitAnnotatedMethods.get(i));
                classPickerDialog.pack();
                classPickerDialog.setTitle(Constants.UIConstants.MAIN_DIALOG_TITLE);
                classPickerDialog.setSize(Constants.UIConstants.DIALOG_WIDTH, Constants.UIConstants.DIALOG_HEIGHT);
                classPickerDialog.setLocation(parentDialog.getLocation());

                final boolean[] breakDueToDialogCancel = {false};
                int index = i;
                classPickerDialog.setOnDialogClosedListener(new DialogClosedListener() {
                    @Override
                    public void onCancelled() {
                        breakDueToDialogCancel[0] = true;
                    }

                    @Override
                    public void onUserConfirm(ReturnedData data) {
                        ClassPickerDialogReturnedData returnedData = (ClassPickerDialogReturnedData) data;
                        System.out.println("ClassPickerDialogReturnedData: modifiedRetrofitAnnotatedMethod:" + returnedData.getModifiedRetrofitAnnotatedMethod());
                        retrofitAnnotatedMethods.set(index, returnedData.getModifiedRetrofitAnnotatedMethod());
                    }
                });
                classPickerDialog.setVisible(true);

                if (breakDueToDialogCancel[0])
                    break;
            }

            String retrofitAnnotatedMethod = Utils.removeHashesAroundReturnType(retrofitAnnotatedMethods.get(i));
            int finalLastCaretPosition = lastCaretPosition;
            WriteCommandAction.runWriteCommandAction(mProject, () -> mEditor.getDocument().insertString(finalLastCaretPosition, "\n" + retrofitAnnotatedMethod + "\n"));
            lastCaretPosition += retrofitAnnotatedMethod.length() + 2 /* 2 '\n'*/;
        }

        mEditor.getCaretModel().getCurrentCaret().moveToOffset(lastCaretPosition);
    }

    private ArrayList<String> constructRetrofitAnnotatedMethods(List<Item> items, boolean isDynamicHeader, String returnTypeFormat)
    {
        ArrayList<String> retrofitAnnotatedMethods = new ArrayList<>();
        for(Item item : items) {
            if (item.getItems() != null && item.getItems().size() > 0) {
                retrofitAnnotatedMethods.addAll(constructRetrofitAnnotatedMethods(item.getItems(), isDynamicHeader, returnTypeFormat));
            }
            else {
                String header = (isDynamicHeader) ? "" : getStaticHeader(item);
                String annotation = getAnnotation(item);
                String method = getMethod(item, isDynamicHeader, returnTypeFormat);
                retrofitAnnotatedMethods.add(header + annotation + method);
            }
        }

        return retrofitAnnotatedMethods;
    }

    private String getStaticHeader(Item item) {
        StringBuilder result = new StringBuilder();

        if(item.getRequest().getHeaders() !=null && item.getRequest().getHeaders().size() > 0) {
            result.append(Utils.getIndentation(INDENT_SIZE))
                    .append("@Headers({");

            for(Item.Request.Header header : item.getRequest().getHeaders()) {
                result.append("\"")
                        .append(Utils.skipQuotes(header.getKey()))
                        .append(": ")
                        .append(Utils.skipQuotes(header.getValue()))
                        .append("\"");

                if(item.getRequest().getHeaders().indexOf(header) != item.getRequest().getHeaders().size() - 1)
                    result.append(",\n              ");
            }
            result.append("})\n");
        }

        return result.toString();
    }

    private String getDynamicHeader(Item item) {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;
        StringBuilder result = new StringBuilder();

        if(item.getRequest().getHeaders()!=null && item.getRequest().getHeaders().size() > 0) {

            for(Item.Request.Header header : item.getRequest().getHeaders()) {
                result.append(RetrofitSyntaxHelper.constructAnnotatedParam(state.getLanguage(), "@Header",
                        header.getKey(), header.getKey().replaceAll("[^A-Za-z0-9()\\[\\]]", "")));

                if(item.getRequest().getHeaders().indexOf(header) != item.getRequest().getHeaders().size() - 1)
                    result.append(",\n              ");
            }
        }

        return result.toString();
    }

    private String getAnnotation(Item item) {
        StringBuilder result = new StringBuilder();
        String httpMethod = item.getRequest().getMethod();
        String url = getApiPath(Utils.getUrlFromGsonObject(item.getRequest().getUrl()));

        // Remove the queries from the url
        if (hasQueries(item))
            url = url.substring(0, url.indexOf("?"));

        // Add form url encoded annotation if needed
        if(item.getRequest().getBody() != null && item.getRequest().getBody() .getUrlencoded() != null)
            result.append(Utils.getIndentation(INDENT_SIZE))
                    .append("@FormUrlEncoded\n");

        result.append(Utils.getIndentation(INDENT_SIZE))
                .append("@")
                .append(httpMethod)
                .append("(\"")
                .append(url)
                .append("\")\n");

        return result.toString();
    }

    private String getApiPath(String url) {
        URI uri;
        try {
            uri = new URI(url);
            String domain = uri.getHost();
            url = url.replace("http://", "");
            url = url.replace("https://", "");
            url = url.replace(domain, "");

            // Replace all path variables
            url = url.replaceAll(":(\\w+)", "{$1}");
            return url;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url;
    }

    private String getMethod(Item item, boolean isDynamicHeader, String returnTypeFormat) {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        String methodName =  item.getName().trim();
        if (methodName.startsWith("http"))
        {
            System.out.println("The item: " + item.getName() + ", seems to be a url, consider updating it to a proper name");
            methodName = item.getRequest().getMethod();
        }

        methodName = Utils.convertToTitleCase(methodName).replace(" ", ""); // Remove any white spaces

        StringBuilder result = new StringBuilder(Utils.getIndentation(INDENT_SIZE));

        String returnType = returnTypeFormat.replace("T", methodName + RETURN_TYPE_POSTFIX);

        String dynamicHeader = "";
        if (isDynamicHeader)
                dynamicHeader = getDynamicHeader(item);

        boolean useCoroutines = state.getReturnTypeRadioButton() == ReturnTypeRadioButton.BUTTON_RETROFIT_AND_COROUTINES;

        result.append(RetrofitSyntaxHelper.constructMethodSignature(state.getLanguage(), returnType, methodName,
                dynamicHeader, addPathParams(item), addFieldParams(item), addQueryParams(item), addBodyParam(item), useCoroutines));

        return result.toString();
    }

    private String addPathParams(Item item) {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;
        StringBuilder resultBuilder = new StringBuilder();

        String url = Utils.getUrlFromGsonObject(item.getRequest().getUrl());
        ArrayList<String> paramList = Utils.extractParamsFromUrlApiPath(getApiPath(url));

        String annotatedParam = paramList.stream()
                .map( param -> RetrofitSyntaxHelper.constructAnnotatedParam(state.getLanguage(), "@Path",
                        param, param)
                ).collect(Collectors.joining(", "));

        resultBuilder.append(annotatedParam);

        return resultBuilder.toString();
    }

    private String addFieldParams(Item item) {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        StringBuilder resultBuilder = new StringBuilder();

        // It is possible to have a Post without a body
        if (item.getRequest().getBody() != null) {
            Item.Request.Body body = item.getRequest().getBody();

            //from Url-encoded
            if (body.getUrlencoded() != null) {

                String annotatedParams = body.getUrlencoded().stream().map(
                        urlencoded -> RetrofitSyntaxHelper.constructAnnotatedParam(state.getLanguage(), "@Field",
                                urlencoded.getKey(), urlencoded.getKey())
                ).collect(Collectors.joining(", "));

                resultBuilder.append(annotatedParams);
            }
            //from form-data
            else if (body.getFormdata() != null) {

                String annotatedParams = body.getFormdata().stream().map(
                        formdata -> RetrofitSyntaxHelper.constructAnnotatedParam(state.getLanguage(), "@Field",
                                formdata.getKey(), formdata.getKey())
                ).collect(Collectors.joining(", "));

                resultBuilder.append(annotatedParams);
            }
        }
        return resultBuilder.toString();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private String addQueryParams(Item item) {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        StringBuilder result = new StringBuilder();

        // TODO: Update Url in the Collection class to be parsed properly from Json
        Object urlObject = item.getRequest().getUrl();

         if(urlObject instanceof LinkedTreeMap) {
             LinkedTreeMap urlMap = (LinkedTreeMap) urlObject;
             if (urlMap.get("query") != null)
             {
                if (urlMap.get("query") instanceof List<?>) {
                    List<LinkedTreeMap> queries = (List<LinkedTreeMap>) urlMap.get("query");

                    String annotatedParams = queries.stream()
                            .filter(
                                    query -> query.get("disabled") == null || (query.get("disabled") != null && !(Boolean) query.get("disabled"))
                            ).map( query ->
                                    RetrofitSyntaxHelper.constructAnnotatedParam(state.getLanguage(), "@Query",
                                    (String) query.get("key"), (String) query.get("key"))
                            ).collect(Collectors.joining(", "));

                    result.append(annotatedParams);
                }
            }
        }

        return result.toString();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean hasQueries(Item item)
    {
        boolean hasQueries = false;

        // TODO: Update Url in the Collection class to be parsed properly from Json
        Object urlObject = item.getRequest().getUrl();

        if(urlObject instanceof LinkedTreeMap) {
            LinkedTreeMap urlMap = (LinkedTreeMap) urlObject;
            if (urlMap.get("query") != null) {
                for (LinkedTreeMap query : (Iterable<? extends LinkedTreeMap>) urlMap.get("query"))
                {
                    if (query.get("disabled") != null && (Boolean) query.get("disabled"))
                    {
                        System.out.println("Found a disabled query: " + query);
                    }
                    else {
                        hasQueries = true;
                        break;
                    }
                }
            }
        }

        return hasQueries;
    }

    private String addBodyParam(Item item) {
        PluginState state = PluginService.getInstance(mProject).getState();
        assert state != null;

        StringBuilder result = new StringBuilder();

        // It is possible to have a Post without a body
        if (item.getRequest().getBody() != null) {
            Item.Request.Body body = item.getRequest().getBody();

            //raw body
            if (body.getMode().equals("raw")) {
                result.append(RetrofitSyntaxHelper.constructAnnotatedParam(state.getLanguage(), "@Body",
                        "body"));
            }
        }

        return result.toString();
    }
}
