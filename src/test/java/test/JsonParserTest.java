package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hamed.postmantoretrofit2v2.Collection;
import com.hamed.postmantoretrofit2v2.Model;
import com.hamed.postmantoretrofit2v2.gsondeserialisers.RequestJsonDeserializer;
import com.hamed.postmantoretrofit2v2.gsondeserialisers.UrlJsonDeserializer;
import com.hamed.postmantoretrofit2v2.pluginstate.helperclasses.enums.Language;
import com.hamed.postmantoretrofit2v2.utils.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonParserTest {

    Gson gson;
    String jsonFilesDirectory = new File("").getAbsolutePath() + "/src/test/java/test/jsonfiles/";

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

        Model model = new Model();

        Collection collection = model.parsePostman(
                JsonFileReader.readJsonFromFile(jsonFilesDirectory +
                        "JsonPlaceholder.postman_collection_requestAsString.json"));

        // The request is assumed to be a GET request
        assert collection != null;
        Assert.assertEquals(collection.getItems().size(), 1);
        Assert.assertEquals(collection.getItems().get(0).getName(), "GetPost");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getMethod(), "GET");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getUrl().getRaw(),
                "https://jsonplaceholder.typicode.com/posts/1");

        ArrayList<String> annotatedMethods = model.constructRetrofitAnnotatedMethods(collection.getItems(),
                false, returnType, Language.JAVA, false, new HashMap<>());

        Assert.assertEquals(annotatedMethods.size(), 1);
        String annotatedMethod = annotatedMethods.get(0);

        // The expected constructed method
        //     @GET(/posts/1)
        //     void Single<GetPostResponse> GetPost();
        String expectedAnnotatedMethod = Utils.getIndentation(4) +
                "@GET(\"/posts/1\")\n" +
                Utils.getIndentation(4) +
                "Single<GetPostResponse> GetPost();";

        Assert.assertEquals(expectedAnnotatedMethod, annotatedMethod);
    }

    @Test
    public void TestParsingCollectionWithUrlAsString() {

        Model model = new Model();

        Collection collection = model.parsePostman(
                JsonFileReader.readJsonFromFile(jsonFilesDirectory +
                        "JsonPlaceholder.postman_collection_urlAsString.json"));

        // The request is assumed to be a GET request
        assert collection != null;
        Assert.assertEquals(collection.getItems().size(), 1);
        Assert.assertEquals(collection.getItems().get(0).getName(), "GetPost");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getMethod(), "GET");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getUrl().getRaw(),
                "https://jsonplaceholder.typicode.com/posts/1");

        ArrayList<String> annotatedMethods = model.constructRetrofitAnnotatedMethods(collection.getItems(),
                false, returnType, Language.JAVA, false, new HashMap<>());

        Assert.assertEquals(annotatedMethods.size(), 1);
        String annotatedMethod = annotatedMethods.get(0);

        // The expected constructed method
        //    @GET("/posts/1")
        //    Single<GetPostResponse> GetPost();
        String expectedAnnotatedMethod = Utils.getIndentation(4) +
                "@GET(\"/posts/1\")\n" +
                Utils.getIndentation(4) +
                "Single<GetPostResponse> GetPost();";

        Assert.assertEquals(expectedAnnotatedMethod, annotatedMethod);
    }

    @Test
    public void TestParsingCollectionWithNormalRequest() {

        Model model = new Model();

        Collection collection = model.parsePostman(
                JsonFileReader.readJsonFromFile(jsonFilesDirectory +
                        "JsonPlaceholder.postman_collection_normalRequest.json"));

        // The request is assumed to be a GET request
        assert collection != null;
        Assert.assertEquals(collection.getItems().size(), 1);
        Assert.assertEquals(collection.getItems().get(0).getName(), "GetPost");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getMethod(), "GET");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getUrl().getRaw(),
                "https://jsonplaceholder.typicode.com/posts/1");

        ArrayList<String> annotatedMethods = model.constructRetrofitAnnotatedMethods(collection.getItems(),
                false, returnType, Language.JAVA, false, new HashMap<>());

        Assert.assertEquals(annotatedMethods.size(), 1);
        String annotatedMethod = annotatedMethods.get(0);

        // The expected constructed method
        //    @GET("/posts/1")
        //    Single<GetPostResponse> GetPost();
        String expectedAnnotatedMethod = Utils.getIndentation(4) +
                "@GET(\"/posts/1\")\n" +
                Utils.getIndentation(4) +
                "Single<GetPostResponse> GetPost();";

        Assert.assertEquals(expectedAnnotatedMethod, annotatedMethod);
    }

    @Test
    public void TestParsingCollectionWithUrlPathParameter() {

        Model model = new Model();

        Collection collection = model.parsePostman(
                JsonFileReader.readJsonFromFile(jsonFilesDirectory +
                        "JsonPlaceholder.postman_collection_urlParam.json"));

        // The request is assumed to be a GET request
        assert collection != null;
        Assert.assertEquals(collection.getItems().size(), 1);
        Assert.assertEquals(collection.getItems().get(0).getName(), "GetPostById");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getMethod(), "GET");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getUrl().getRaw(),
                "https://jsonplaceholder.typicode.com/posts/:id");

        ArrayList<String> annotatedMethods = model.constructRetrofitAnnotatedMethods(collection.getItems(),
                false, returnType, Language.JAVA, false, new HashMap<>());

        Assert.assertEquals(annotatedMethods.size(), 1);
        String annotatedMethod = annotatedMethods.get(0);

        // The expected constructed method
        //    @GET("/posts/{id}")
        //    Single<GetPostResponse> GetPost(@Path("id") String id);
        String expectedAnnotatedMethod = Utils.getIndentation(4) +
                "@GET(\"/posts/{id}\")\n" +
                Utils.getIndentation(4) +
                "Single<GetPostByIdResponse> GetPostById(@Path(\"id\") String id);";

        Assert.assertEquals(expectedAnnotatedMethod, annotatedMethod);
    }

    @Test
    public void TestParsingCollectionWithUrlEncoded() {

        Model model = new Model();

        Collection collection = model.parsePostman(
                JsonFileReader.readJsonFromFile(jsonFilesDirectory +
                        "JsonPlaceholder.postman_collection_urlEncoded.json"));

        // The request is assumed to be a GET request
        assert collection != null;
        Assert.assertEquals(collection.getItems().size(), 1);
        Assert.assertEquals(collection.getItems().get(0).getName(), "GetPostById");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getMethod(), "GET");
        Assert.assertEquals(collection.getItems().get(0).getRequest().getUrl().getRaw(),
                "https://jsonplaceholder.typicode.com/posts");

        ArrayList<String> annotatedMethods = model.constructRetrofitAnnotatedMethods(collection.getItems(),
                false, returnType, Language.JAVA, false, new HashMap<>());

        Assert.assertEquals(annotatedMethods.size(), 1);
        String annotatedMethod = annotatedMethods.get(0);

        // The expected constructed method
        //    @FormUrlEncoded
        //    @GET("/posts")
        //    Single<GetPostResponse> GetPost(@Field("id") String id);
        String expectedAnnotatedMethod = Utils.getIndentation(4) +
                "@FormUrlEncoded\n" +
                Utils.getIndentation(4) +
                "@GET(\"/posts\")\n" +
                Utils.getIndentation(4) +
                "Single<GetPostByIdResponse> GetPostById(@Field(\"id\") String id);";

        Assert.assertEquals(expectedAnnotatedMethod, annotatedMethod);
    }
}
