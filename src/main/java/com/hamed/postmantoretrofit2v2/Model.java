package com.hamed.postmantoretrofit2v2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hamed.postmantoretrofit2v2.Collection.Item;
import com.hamed.postmantoretrofit2v2.classgeneration.ResponseClassGenerator;
import com.hamed.postmantoretrofit2v2.forms.ClassPickerDialog;
import com.hamed.postmantoretrofit2v2.forms.listeners.ClassPickerDialogReturnedData;
import com.hamed.postmantoretrofit2v2.forms.listeners.DialogClosedListener;
import com.hamed.postmantoretrofit2v2.forms.listeners.ReturnedData;
import com.hamed.postmantoretrofit2v2.gsondeserialisers.RequestJsonDeserializer;
import com.hamed.postmantoretrofit2v2.gsondeserialisers.UrlJsonDeserializer;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.Language;
import com.hamed.postmantoretrofit2v2.utils.DependencyPluginHelper;
import com.hamed.postmantoretrofit2v2.utils.RetrofitSyntaxHelper;
import com.hamed.postmantoretrofit2v2.utils.Utils;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import javax.annotation.Nullable;
import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Model {

    public static final String RETURN_TYPE_POSTFIX = "Response";

    private int INDENT_SIZE = 4;

    @Nullable
    public Collection parsePostman(String jsonString) {
        try{
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Collection.Item.Request.Url.class, new UrlJsonDeserializer())
                    .registerTypeAdapter(Collection.Item.Request.class, new RequestJsonDeserializer())
                    .create();

            Collection collection = gson.fromJson(jsonString, Collection.class);
            System.out.println(collection);
            if (collection != null && !collection.isValid())
                throw new Exception("Invalid collection");
            return collection;
        } catch (Exception e){
            System.out.println("Json parse failed due to: " + e);
        }
        return null;
    }

    public void generateRetrofitCode(Project project, Editor editor, List<Item> items, boolean isDynamicHeader, UserSettings userSettings, JDialog parentDialog) {

        int lastCaretPosition = editor.getCaretModel().getOffset();

        INDENT_SIZE = userSettings.getIndentSize();

        HashMap<Item, String> generatedClasses = new HashMap<>();

        String returnTypeFormat = Utils.highlightReturnTypeWithHashes(userSettings.getReturnType());

        if (userSettings.automaticallyGenerateClassesFromResponses())
            if (DependencyPluginHelper.isPluginUsable(project, "RoboPojoGenerator", "com.robohorse.robopojogenerator"))
                generatedClasses = generateClassFromResponse(project, items, userSettings);

        ArrayList<String> retrofitAnnotatedMethods = constructRetrofitAnnotatedMethods(items, isDynamicHeader, returnTypeFormat, userSettings.getLanguage(), userSettings.useCoroutines(), generatedClasses);

        for (int i = 0; i < retrofitAnnotatedMethods.size(); i++) {

            if (userSettings.promptToSelectClassForReturnType()) {

                ClassPickerDialog classPickerDialog = new ClassPickerDialog(parentDialog, project, editor, retrofitAnnotatedMethods.get(i));
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
            WriteCommandAction.runWriteCommandAction(project, () -> editor.getDocument().insertString(finalLastCaretPosition, "\n" + retrofitAnnotatedMethod + "\n"));
            lastCaretPosition += retrofitAnnotatedMethod.length() + 2 /* 2 '\n'*/;
        }

        editor.getCaretModel().getCurrentCaret().moveToOffset(lastCaretPosition);
    }

    private HashMap<Item, String> generateClassFromResponse(Project project, List<Item> items, UserSettings userSettings) {

        HashMap<Item, String> mappedGeneratedClasses = new HashMap<>();
        for (Item item: items) {
            if (item.getItems() != null && item.getItems().size() > 0) {
                mappedGeneratedClasses.putAll(generateClassFromResponse(project, item.getItems(), userSettings));
            }
            else {
                if (item.getResponse().size() > 0) {
                    // The class generation uses only the first response
                    Item.Response response = item.getResponse().get(0);
                    String className = response.getName();
                    String jsonBody = response.getBody();
                    if(ResponseClassGenerator.generateClasses(project, userSettings.getReturnTypeClassesDirectory() , className, jsonBody, userSettings.getLanguage(), userSettings.getFramework(), userSettings.getAutomaticClassGenerationOptions()))
                        mappedGeneratedClasses.put(item, className);
                }
            }
        }

        return mappedGeneratedClasses;
    }

    public ArrayList<String> constructRetrofitAnnotatedMethods(List<Item> items, boolean isDynamicHeader, String returnTypeFormat, Language language, boolean useCoroutines, HashMap<Item, String> generatedClasses)
    {
        ArrayList<String> retrofitAnnotatedMethods = new ArrayList<>();
        for(Item item : items) {
            if (item.getItems() != null && item.getItems().size() > 0) {
                retrofitAnnotatedMethods.addAll(constructRetrofitAnnotatedMethods(item.getItems(), isDynamicHeader, returnTypeFormat, language, useCoroutines, generatedClasses));
            }
            else {
                String header = (isDynamicHeader) ? "" : getStaticHeader(item);
                String annotation = getAnnotation(item);
                String method = getMethod(item, isDynamicHeader, returnTypeFormat, language, useCoroutines, generatedClasses);
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
                    result.append(",\n");
            }
            result.append("})\n");
        }

        return result.toString();
    }

    private String getDynamicHeader(Item item, Language language) {
        StringBuilder result = new StringBuilder();

        if(item.getRequest().getHeaders()!=null && item.getRequest().getHeaders().size() > 0) {

            for(Item.Request.Header header : item.getRequest().getHeaders()) {
                result.append(RetrofitSyntaxHelper.constructAnnotatedParam(language, "@Header",
                        header.getKey(), header.getKey().replaceAll("[^A-Za-z0-9()\\[\\]]", "")));

                if(item.getRequest().getHeaders().indexOf(header) != item.getRequest().getHeaders().size() - 1)
                    result.append(",\n");
            }
        }

        return result.toString();
    }

    private String getAnnotation(Item item) {
        StringBuilder result = new StringBuilder();
        String httpMethod = item.getRequest().getMethod();
        String url = getApiPath(item.getRequest().getUrl().getRaw());

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

            try {
                url = url.replace(domain, "");
            } catch (NullPointerException e) {
                throw new NullPointerException("Couldn't get the domain for URL: " + uri + "\n" + e.getMessage());
            }

            // Replace all path variables
            url = url.replaceAll(":(\\w+)", "{$1}");
            return url;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url;
    }

    private String getMethod(Item item, boolean isDynamicHeader, String returnTypeFormat, Language language, boolean useCoroutines, HashMap<Item, String> generatedClasses) {

        String methodName = item.getName().trim();
        if (methodName.startsWith("http")) {
            System.out.println("The item: " + item.getName() + ", seems to be a url, consider updating it to a proper name");
            methodName = item.getRequest().getMethod();
        }

        methodName = Utils.convertToTitleCase(methodName).replace(" ", ""); // Remove any white spaces

        String returnType;
        if (generatedClasses.size() > 0 && generatedClasses.containsKey(item)) {
            returnType = returnTypeFormat.replace("T", generatedClasses.get(item));
        }
        else {
            returnType = returnTypeFormat.replace("T", methodName + RETURN_TYPE_POSTFIX);
        }

        String dynamicHeader = "";
        if (isDynamicHeader) {
            dynamicHeader = getDynamicHeader(item, language);
        }

        return Utils.getIndentation(INDENT_SIZE) + RetrofitSyntaxHelper.constructMethodSignature(language,
                returnType,
                methodName,
                dynamicHeader,
                addPathParams(item, language),
                addFieldParams(item, language),
                addQueryParams(item, language),
                addBodyParam(item, language),
                useCoroutines);
    }

    private String addPathParams(Item item, Language language) {

        StringBuilder resultBuilder = new StringBuilder();

        String url = item.getRequest().getUrl().getRaw();
        ArrayList<String> paramList = Utils.extractParamsFromUrlApiPath(getApiPath(url));

        String annotatedParam = paramList.stream()
                .map( param -> RetrofitSyntaxHelper.constructAnnotatedParam(language, "@Path",
                        param, param)
                ).collect(Collectors.joining(", "));

        resultBuilder.append(annotatedParam);

        return resultBuilder.toString();
    }

    private String addFieldParams(Item item, Language language) {

        StringBuilder resultBuilder = new StringBuilder();

        // It is possible to have a Post without a body
        if (item.getRequest().getBody() != null) {
            Item.Request.Body body = item.getRequest().getBody();

            //from Url-encoded
            if (body.getMode().equals("urlencoded")) {
                String annotatedParams = body.getUrlencoded().stream().map(
                        urlencoded -> RetrofitSyntaxHelper.constructAnnotatedParam(language, "@Field",
                                urlencoded.getKey(), urlencoded.getKey())
                ).collect(Collectors.joining(", "));

                resultBuilder.append(annotatedParams);
            }
            //from form-data
            else if (body.getMode().equals("formdata")) {

                String annotatedParams = body.getFormdata().stream().map(
                        formdata -> RetrofitSyntaxHelper.constructAnnotatedParam(language, "@Field",
                                formdata.getKey(), formdata.getKey())
                ).collect(Collectors.joining(", "));

                resultBuilder.append(annotatedParams);
            }
        }
        return resultBuilder.toString();
    }

    private String addQueryParams(Item item, Language language) {

        StringBuilder result = new StringBuilder();
        List<Item.Request.Url.Query> queries = item.getRequest().getUrl().getQueries();

        if (queries != null)
        {
            String annotatedParams = queries.stream()
                    .filter(
                            query -> !query.isDisabled()
                    ).map( query ->
                            RetrofitSyntaxHelper.constructAnnotatedParam(language, "@Query",
                                    query.getKey(), query.getKey())
                    ).collect(Collectors.joining(", "));

            result.append(annotatedParams);
        }

        return result.toString();
    }

    private boolean hasQueries(Item item)
    {
        boolean hasQueries = false;
        List<Item.Request.Url.Query> queries = item.getRequest().getUrl().getQueries();

        if (queries != null)
        {
            for (Item.Request.Url.Query query : queries)
            {
                if (query.isDisabled())
                {
                    // Skip this query
                    System.out.println("Found a disabled query: " + query);
                }
                else {
                    hasQueries = true;
                    break;
                }
            }
        }

        return hasQueries;
    }

    private String addBodyParam(Item item, Language language) {

        StringBuilder result = new StringBuilder();

        // It is possible to have a Post without a body
        if (item.getRequest().getBody() != null) {
            Item.Request.Body body = item.getRequest().getBody();

            //raw body
            if (body.getMode().equals("raw")) {
                result.append(RetrofitSyntaxHelper.constructAnnotatedParam(language, "@Body",
                        "body"));
            }
        }

        return result.toString();
    }
}
