package com.hamed.postmantoretrofit2v2.gsondeserialisers;

import com.google.gson.*;
import com.hamed.postmantoretrofit2v2.Collection;

import java.lang.reflect.Type;

public class UrlJsonDeserializer implements JsonDeserializer<Collection.Item.Request.Url> {

    @Override
    public Collection.Item.Request.Url deserialize
            (JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if (jsonElement.isJsonObject()) {
            Gson gson = new Gson();
            return gson.fromJson(jsonElement, Collection.Item.Request.Url.class);
        } else {
            Collection.Item.Request.Url url = new Collection.Item.Request.Url();
            url.setRaw(jsonElement.getAsString());
            return url;
        }
    }
}
