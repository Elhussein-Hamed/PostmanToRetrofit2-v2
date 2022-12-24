package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hamed.postmantoretrofit2v2.Collection;
import com.hamed.postmantoretrofit2v2.Model;
import com.hamed.postmantoretrofit2v2.gsondeserialisers.RequestJsonDeserializer;
import com.hamed.postmantoretrofit2v2.gsondeserialisers.UrlJsonDeserializer;
import com.hamed.postmantoretrofit2v2.pluginstate.Language;
import com.hamed.postmantoretrofit2v2.utils.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

public class JsonParserTest {

    Gson gson;
    String testDirectory = new File("").getAbsolutePath() + "/src/main/java/test/";
    String newLine = System.getProperty("line.separator");

    // Use a single return type for all the test cases
    String returnType = "Single<T>";

    @Before
    public void init() {
        gson = new GsonBuilder()
            .registerTypeAdapter(Collection.Item.Request.Url.class, new UrlJsonDeserializer())
            .registerTypeAdapter(Collection.Item.Request.class, new RequestJsonDeserializer())
            .create();
    }

    @Test
    public void TestParsingCollectionWithRequestAsString() {

        Collection collection = gson.fromJson(
                JsonFileReader.readJsonFromFile(testDirectory +
                        "jsonfiles/JsonPlaceholder.postman_collection_requestAsString.json")
                , Collection.class);

        System.out.println("Collection: " + collection);

        // The request is assumed to be a GET request
        Assert.assertEquals(collection.getItems().size(), 1);
        Assert.assertEquals(collection.getItems().get(0).getName(), "GetPost");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getMethod(), "GET");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getUrl().getRaw(),
                "https://jsonplaceholder.typicode.com/post/1");

        Model model = new Model();
        ArrayList<String> annotatedMethods = model.constructRetrofitAnnotatedMethods(collection.getItems(),
                false, returnType, Language.JAVA, false);

        Assert.assertEquals(annotatedMethods.size(), 1);
        String annotatedMethod = annotatedMethods.get(0);

        // The expected constructed method
        //     @GET(/post/1)
        //     void Single<GetPostResponse> GetPost();
        String expectedAnnotatedMethod = Utils.getIndentation(4) +
                "@GET(\"/post/1\")" +
                newLine +
                Utils.getIndentation(4) +
                "Single<GetPostResponse> GetPost();";

        Assert.assertEquals(expectedAnnotatedMethod, annotatedMethod);
    }
}
