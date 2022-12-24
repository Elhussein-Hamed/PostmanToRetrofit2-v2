package com.hamed.postmantoretrofit2v2.gsondeserialisers;

import com.google.gson.*;
import com.hamed.postmantoretrofit2v2.Collection;

import java.lang.reflect.Type;

public class RequestJsonDeserializer implements JsonDeserializer<Collection.Item.Request> {

    @Override
    public Collection.Item.Request deserialize
            (JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if (jsonElement.isJsonObject()) {
            JsonObject requestJsonObject = jsonElement.getAsJsonObject();
            Collection.Item.Request request = new Collection.Item.Request();
            request.setUrl(context.deserialize(requestJsonObject.get("url"), Collection.Item.Request.Url.class));
            request.setMethod(requestJsonObject.get("method").getAsString());
            request.setBody(context.deserialize(requestJsonObject.get("body"), Collection.Item.Request.Body.class));
            return request;
        } else {
            // Request as String
            // According to the Postman documentation (https://www.postmanlabs.com/postman-collection/tutorial-concepts.html)
            // If specified as a string, it is assumed to be a GET request.

            Collection.Item.Request request = new Collection.Item.Request();
            Collection.Item.Request.Url url = new Collection.Item.Request.Url();
            url.setRaw(jsonElement.getAsString());
            request.setUrl(url);
            request.setMethod("GET");
            return request;
        }
    }
}
