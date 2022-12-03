package com.hamed.postmantoretrofit2v2;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.hamed.postmantoretrofit2v2.forms.ClassPickerDialog;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Model {

    public static final String RESPONSE_POSTFIX = "Response";

    private final Project mProject;
    private final Editor mEditor;

    public Model(Project project, Editor editor) {
        mProject = project;
        mEditor = editor;
    }

    public Collection parsePostman(String jsonString) {
        try{
            Collection collection = new Gson().fromJson(jsonString, Collection.class);
            System.out.println(collection);
            return collection;
        } catch (Exception e){
            System.out.println("Json parse failed.");
        }
        return null;
    }

    public void generateRxJavaCode(List<Collection.ItemBean> items, boolean isDynamicHeader, String responseFormat) {

        int lastCaretPosition = mEditor.getCaretModel().getOffset();

        ArrayList<String> retrofitAnnotatedMethods = constructRetrofitAnnotatedMethods(items, isDynamicHeader, responseFormat);

        for (String retrofitAnnotatedMethod : retrofitAnnotatedMethods) {

            PluginState state = PluginService.getInstance(mProject).getState();
            if (state.getPromptToSelectClassForResponseType()) {

                ClassPickerDialog classPickerDialog = new ClassPickerDialog(mProject, mEditor, retrofitAnnotatedMethod);
                classPickerDialog.pack();
                classPickerDialog.setTitle("Postman To Retrofit2 V2");
                classPickerDialog.setSize(600, 400);
                classPickerDialog.setLocationRelativeTo(null);
                classPickerDialog.setVisible(true);

                System.out.println("classPickerDialog.getClassName():" + classPickerDialog.getModifiedRetrofitAnnotatedMethod());
                if (classPickerDialog.getModifiedRetrofitAnnotatedMethod() != null) {
                    retrofitAnnotatedMethod = classPickerDialog.getModifiedRetrofitAnnotatedMethod();
                }
            }

            int finalLastCaretPosition = lastCaretPosition;
            String finalRetrofitAnnotatedMethod = retrofitAnnotatedMethod;
            WriteCommandAction.runWriteCommandAction(mProject, () -> mEditor.getDocument().insertString(finalLastCaretPosition, "\n" + finalRetrofitAnnotatedMethod + "\n"));
            lastCaretPosition += retrofitAnnotatedMethod.length() + 2 /* 2 '\n'*/;
        }

        mEditor.getCaretModel().getCurrentCaret().moveToOffset(lastCaretPosition);
    }

    private ArrayList<String> constructRetrofitAnnotatedMethods(List<Collection.ItemBean> items, boolean isDynamicHeader, String responseFormat)
    {
        ArrayList<String> retrofitAnnotatedMethods = new ArrayList<>();
        for(Collection.ItemBean item : items) {
            if (item.getItem() != null && item.getItem().size() > 0) {
                retrofitAnnotatedMethods.addAll(constructRetrofitAnnotatedMethods(item.getItem(), isDynamicHeader, responseFormat));
            }
            else {
                String header = (isDynamicHeader) ? "" : getStaticHeader(item);
                String annotation = getAnnotation(item);
                String method = getMethod(item, isDynamicHeader, responseFormat);
                retrofitAnnotatedMethods.add(header + annotation + method);
            }
        }

        return retrofitAnnotatedMethods;
    }

    private String getStaticHeader(Collection.ItemBean item) {
        StringBuilder result = new StringBuilder();
        if(item.getRequest().getHeader()!=null && item.getRequest().getHeader().size()>0) {
            result = new StringBuilder("    @Headers({");
            for(Collection.ItemBean.RequestBean.HeaderBean header : item.getRequest().getHeader()) {
                result.append("\"").append(Utils.skipQuotes(header.getKey())).append(": ").append(Utils.skipQuotes(header.getValue())).append("\"");
                if(item.getRequest().getHeader().indexOf(header) != item.getRequest().getHeader().size()-1) result.append(",\n              ");
            }
            result.append("})\n");
        }
        return result.toString();
    }

    private String getDynamicHeader(Collection.ItemBean item) {
        StringBuilder result = new StringBuilder();
        if(item.getRequest().getHeader()!=null && item.getRequest().getHeader().size() > 0) {
            for(Collection.ItemBean.RequestBean.HeaderBean headerBean : item.getRequest().getHeader()) {
                result.append("@Header(\"").append(headerBean.getKey()).append("\")").append("String ").append(headerBean.getKey().replaceAll("[^A-Za-z0-9()\\[\\]]", "")).append(", ");
            }
        }
        return result.toString();
    }

    private String getAnnotation(Collection.ItemBean item) {
        String result = "";
        if(!item.getRequest().getMethod().equalsIgnoreCase("GET")) result += "    @FormUrlEncoded\n";
        result += "    @" + item.getRequest().getMethod();
        if(item.getRequest().getUrl() instanceof String) result += "(\"" + getApiPath((String)item.getRequest().getUrl(), false) + "\")";
        else if(item.getRequest().getUrl() instanceof LinkedTreeMap) {
            LinkedTreeMap url = (LinkedTreeMap)item.getRequest().getUrl();
            result += "(\"" + getApiPath(url.get("raw").toString(), false) + "\")";
        }
        return result + "\n";

    }

    private String getApiPath(String url, boolean hasDomain) {
        URI uri;
        try {
            if(!hasDomain) {
                uri = new URI(url);
                String domain = uri.getHost();
                url = url.replace("http://", "");
                url = url.replace("https://", "");
                url = url.replace(domain, "");
                return url;
            }
            return url;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return url;
    }

    private String getMethod(Collection.ItemBean item, boolean isDynamicHeader, String responseFormat) {
        String method =  item.getName().trim();
        if (method.startsWith("http"))
        {
            System.out.println("The item: " + item.getName() + ", seems to be a url, consider updating it to a proper name");
            method = item.getRequest().getMethod();
        }

        method = Utils.convertToTitleCase(method);
        method = method.replace(" ", ""); // Remove any white spaces

        String response = responseFormat.replace("<T>", "<" + method + RESPONSE_POSTFIX + ">");
        String result = "    " + response + " " + method + "(";
        if(isDynamicHeader) result += getDynamicHeader(item);
        if(item.getRequest().getMethod().equalsIgnoreCase("GET")) result = addQueryParams(item, result);
        else result = addFieldParams(item, result);

        return result;
    }

    private String addFieldParams(Collection.ItemBean item, String result) {

        // It is possible to have a Post without a body
        if (item.getRequest().getBody() != null) {
            //from Url-encoded
            if (item.getRequest().getBody().getUrlencoded() != null) {
                StringBuilder resultBuilder = new StringBuilder(result);
                for (Collection.ItemBean.RequestBean.BodyBean.UrlencodedBean urlencoded : item.getRequest().getBody().getUrlencoded()) {
                    resultBuilder.append("@Field(\"").append(urlencoded.getKey()).append("\") ").append("String ").append(urlencoded.getKey());
                    if (item.getRequest().getBody().getUrlencoded().indexOf(urlencoded) != item.getRequest().getBody().getUrlencoded().size() - 1)
                        resultBuilder.append(", ");
                }
                result = resultBuilder.toString();
                return result + ");";
            }

            //from form-data
            if (item.getRequest().getBody().getFormdata() != null) {
                StringBuilder resultBuilder = new StringBuilder(result);
                for (Collection.ItemBean.RequestBean.BodyBean.FormdataBean formdata : item.getRequest().getBody().getFormdata()) {
                    resultBuilder.append("@Field(\"").append(formdata.getKey()).append("\") ").append("String ").append(formdata.getKey());
                    if (item.getRequest().getBody().getFormdata().indexOf(formdata) != item.getRequest().getBody().getFormdata().size() - 1)
                        resultBuilder.append(", ");
                }
                result = resultBuilder.toString();
                return result + ");";
            }
        }
        return result+");";
    }

    private String addQueryParams(Collection.ItemBean item, String result) {
        return result+");";
    }
}
