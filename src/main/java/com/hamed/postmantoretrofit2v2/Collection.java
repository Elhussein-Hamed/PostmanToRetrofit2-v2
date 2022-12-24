package com.hamed.postmantoretrofit2v2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model class used by Gson to parse Postman collection Json
 */
@SuppressWarnings("unused")
public class Collection {

    private Info info;
    private List<?> variables;
    @SerializedName("item")
    private List<Item> items;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<?> getVariables() {
        return variables;
    }

    public void setVariables(List<?> variables) {
        this.variables = variables;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public boolean isValid() { return !(this.info == null && this.variables == null && this.items == null); }

    @Override
    public String toString() {
        return "Collection{" +
                "info=" + info +
                ", variables=" + variables +
                ", item=" + items +
                '}';
    }

    public static class Info {

        private String name;
        private String _postman_id;
        private String description;
        private String schema;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String get_postman_id() {
            return _postman_id;
        }

        public void set_postman_id(String _postman_id) {
            this._postman_id = _postman_id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "name='" + name + '\'' +
                    ", _postman_id='" + _postman_id + '\'' +
                    ", description='" + description + '\'' +
                    ", schema='" + schema + '\'' +
                    '}';
        }
    }

    public static class VariableItem {

        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "VariableItem{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class Item {

        private String id;
        private String name;
        private Request request;
        @SerializedName("item")
        private List<Item> items;
        private List<?> response;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Item> getItems() { return items; }

        public void setItems(List<Item> items) { this.items = items; }

        public Request getRequest() {
            return request;
        }

        public void setRequest(Request request) {
            this.request = request;
        }

        public List<?> getResponse() {
            return response;
        }

        public void setResponse(List<?> response) {
            this.response = response;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", request=" + request +
                    ", item=" + items +
                    ", response=" + response +
                    '}';
        }

        public static class Request {
            private Url url;
            private String method;
            private Body body;
            private String description;

            @SerializedName("header")
            private List<Header> headers;

            public Url getUrl() {
                return url;
            }

            public void setUrl(Url url) {
                this.url = url;
            }

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            public Body getBody() {
                return body;
            }

            public void setBody(Body body) {
                this.body = body;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public List<Header> getHeaders() {
                return headers;
            }

            public void setHeaders(List<Header> headers) {
                this.headers = headers;
            }

            @Override
            public String toString() {
                return "Request{" +
                        "url=" + url +
                        ", method='" + method + '\'' +
                        ", body=" + body +
                        ", description='" + description + '\'' +
                        ", headers=" + headers +
                        '}';
            }

            public static class Url {

                @SerializedName("path")
                private List<String> path;

                @SerializedName("protocol")
                private String protocol;

                @SerializedName("host")
                private List<String> host;

                @SerializedName("variable")
                private List<VariableItem> variable;

                @SerializedName("raw")
                private String raw;

                @SerializedName("query")
                private List<Query> queries;

                public void setPath(List<String> path){
                    this.path = path;
                }

                public List<String> getPath(){
                    return path;
                }

                public void setProtocol(String protocol){
                    this.protocol = protocol;
                }

                public String getProtocol(){
                    return protocol;
                }

                public void setHost(List<String> host){
                    this.host = host;
                }

                public List<String> getHost(){
                    return host;
                }

                public void setVariable(List<VariableItem> variable){
                    this.variable = variable;
                }

                public List<VariableItem> getVariable(){
                    return variable;
                }

                public void setRaw(String raw){
                    this.raw = raw;
                }

                public String getRaw(){
                    return raw;
                }

                public List<Query> getQueries() {
                    return queries;
                }

                public void setQueries(List<Query> queries) {
                    this.queries = queries;
                }

                @Override
                public String toString() {
                    return "Url{" +
                            "path=" + path +
                            ", protocol='" + protocol + '\'' +
                            ", host=" + host +
                            ", variable=" + variable +
                            ", raw='" + raw + '\'' +
                            ", queries=" + queries +
                            '}';
                }

                public static class Query {
                    private String key;
                    private String value;

                    private boolean disabled;

                    public String getKey() {
                        return key;
                    }

                    public void setKey(String key) {
                        this.key = key;
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }

                    public boolean isDisabled() {
                        return disabled;
                    }

                    public void setDisabled(boolean disabled) {
                        this.disabled = disabled;
                    }

                    @Override
                    public String toString() {
                        return "Query{" +
                                "key='" + key + '\'' +
                                ", value='" + value + '\'' +
                                ", disabled=" + disabled +
                                '}';
                    }
                }
            }

            public static class Body {
                private String mode;
                private List<Urlencoded> urlencoded;
                private List<Formdata> formdata;

                public String getMode() {
                    return mode;
                }

                public void setMode(String mode) {
                    this.mode = mode;
                }

                public List<Urlencoded> getUrlencoded() {
                    return urlencoded;
                }

                public void setUrlencoded(List<Urlencoded> urlencoded) {
                    this.urlencoded = urlencoded;
                }

                public List<Formdata> getFormdata() {
                    return formdata;
                }

                public void setFormdata(List<Formdata> formdata) {
                    this.formdata = formdata;
                }

                public static class Urlencoded {
                    private String key;
                    private String value;
                    private String description;
                    private String type;

                    public String getKey() {
                        return key;
                    }

                    public void setKey(String key) {
                        this.key = key;
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }

                    public String getDescription() {
                        return description;
                    }

                    public void setDescription(String description) {
                        this.description = description;
                    }

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    @Override
                    public String toString() {
                        return "Urlencoded{" +
                                "key='" + key + '\'' +
                                ", value='" + value + '\'' +
                                ", description='" + description + '\'' +
                                ", type='" + type + '\'' +
                                '}';
                    }
                }

                public static class Formdata {
                    private String key;
                    private String value;
                    private String type;

                    public String getKey() {
                        return key;
                    }

                    public void setKey(String key) {
                        this.key = key;
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    @Override
                    public String toString() {
                        return "Formdata{" +
                                "key='" + key + '\'' +
                                ", value='" + value + '\'' +
                                ", type='" + type + '\'' +
                                '}';
                    }
                }
            }

            public static class Header {
                private String key;
                private String value;
                private String description;

                public String getKey() {
                    return key;
                }

                public void setKey(String key) {
                    this.key = key;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                @Override
                public String toString() {
                    return "Header{" +
                            "key='" + key + '\'' +
                            ", value='" + value + '\'' +
                            ", description='" + description + '\'' +
                            '}';
                }
            }
        }
    }
}
