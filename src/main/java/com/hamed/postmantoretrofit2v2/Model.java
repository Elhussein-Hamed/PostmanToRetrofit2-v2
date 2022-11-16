package com.hamed.postmantoretrofit2v2;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Model {
    private final Project mProject;
    private final Editor mEditor;

    Model(Project project, Editor editor) {
        mProject = project;
        mEditor = editor;
    }

    Collection parsePostman(String jsonString) {
        try{
            Collection collection = new Gson().fromJson(jsonString, Collection.class);
            System.out.println(collection);
            return collection;
        } catch (Exception e){
            System.out.println("Json parse failed.");
        }
        return null;
    }

    void generateRxJavaCode(List<Collection.ItemBean> items, boolean isDynamicHeader, String responseFormat) {

        int lastCaretPosition =  mEditor.getCaretModel().getOffset();

        // Moving the caret after each item write doesn't write them correctly within the function body hence
        // keep the caret in the same place and invert the list instead to write the items in the correct order
        for(Collection.ItemBean item : items) {
            if (item.getItem() != null && item.getItem().size() > 0) {
                generateRxJavaCode(item.getItem(), isDynamicHeader, responseFormat);
                lastCaretPosition =  mEditor.getCaretModel().getOffset();
            }
            else {
                String header = (isDynamicHeader) ? "" : getStaticHeader(item);
                String annotation = getAnnotation(item);
                String method = getMethod(item, isDynamicHeader, responseFormat);
                int finalLastCaretPosition = lastCaretPosition;
                WriteCommandAction.runWriteCommandAction(mProject, () -> mEditor.getDocument().insertString(finalLastCaretPosition,  "\n" + header + annotation + method + "\n"));
                lastCaretPosition += header.length() + annotation.length() + method.length() + 2 /* 2 '\n'*/;
                mEditor.getCaretModel().getCurrentCaret().moveToOffset(lastCaretPosition);
            }
        }
        mEditor.getCaretModel().getCurrentCaret().moveToOffset(lastCaretPosition);
    }

    private String getStaticHeader(Collection.ItemBean item) {
        String result = "";
        if(item.getRequest().getHeader()!=null && item.getRequest().getHeader().size()>0) {
            result = "    @Headers({";
            for(Collection.ItemBean.RequestBean.HeaderBean header : item.getRequest().getHeader()) {
                result += "\"" + Utils.skipQuotes(header.getKey()) + ": " + Utils.skipQuotes(header.getValue()) + "\"";
                if(item.getRequest().getHeader().indexOf(header) != item.getRequest().getHeader().size()-1) result += ",\n              ";
            }
            result += "})\n";
        }
        return result;
    }

    private String getDynamicHeader(Collection.ItemBean item) {
        String result = "";
        if(item.getRequest().getHeader()!=null && item.getRequest().getHeader().size() > 0) {
            for(Collection.ItemBean.RequestBean.HeaderBean headerBean : item.getRequest().getHeader()) {
                result += "@Header(\""+headerBean.getKey()+"\")" + "String " + headerBean.getKey().replaceAll("[^A-Za-z0-9()\\[\\]]", "") + ", ";
            }
        }
        return result;
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
        }
        else {
            method = Utils.convertToTitleCase(method);
            method = method.replace(" ", ""); // Remove any white spaces
        }

        String response = responseFormat.replace("<T>", "<" + method + "Response>");
        String result = "    " + response + " " + method + "(";
        if(isDynamicHeader) result += getDynamicHeader(item);
        if(item.getRequest().getMethod().equalsIgnoreCase("GET")) result = addQueryParams(item, result);
        else result = addFieldParams(item, result);
        return result;
    }

    private String addFieldParams(Collection.ItemBean item, String result) {
        //from Url-encoded
        if(item.getRequest().getBody().getUrlencoded()!=null) {
            for (Collection.ItemBean.RequestBean.BodyBean.UrlencodedBean urlencoded : item.getRequest().getBody().getUrlencoded()) {
                result += "@Field(\"" + urlencoded.getKey() + "\") " + "String " + urlencoded.getKey();
                if (item.getRequest().getBody().getUrlencoded().indexOf(urlencoded) != item.getRequest().getBody().getUrlencoded().size() - 1)
                    result += ", ";
            }
            return result + ");";
        }
        //from form-data
        if(item.getRequest().getBody().getFormdata()!=null) {
            for (Collection.ItemBean.RequestBean.BodyBean.FormdataBean formdata : item.getRequest().getBody().getFormdata()) {
                result += "@Field(\"" + formdata.getKey() + "\") " + "String " + formdata.getKey();
                if (item.getRequest().getBody().getFormdata().indexOf(formdata) != item.getRequest().getBody().getFormdata().size() - 1)
                    result += ", ";
            }
            return result + ");";
        }
        return result+");";
    }

    private String addQueryParams(Collection.ItemBean item, String result) {
        return result+");";
    }
}
