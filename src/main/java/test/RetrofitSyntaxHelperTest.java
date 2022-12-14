package test;

import com.hamed.postmantoretrofit2v2.pluginstate.Language;
import com.hamed.postmantoretrofit2v2.utils.RetrofitSyntaxHelper;
import org.junit.Assert;
import org.junit.Test;

public class RetrofitSyntaxHelperTest {

    @Test
    public void constructAnnotatedParam_header() {

        // Test the header annotation in Java
        Assert.assertEquals(RetrofitSyntaxHelper.constructAnnotatedParam(Language.JAVA, "@Header",
                "header", "header"), "@Header(\"header\") String header");

        // Test the header annotation in Kotlin
        Assert.assertEquals(RetrofitSyntaxHelper.constructAnnotatedParam(Language.KOTLIN, "@Header",
                "header", "header"), "@Header(\"header\") header: String");

    }
    @Test
    public void constructAnnotatedParam_field() {

        // Test the field annotation in Java
        Assert.assertEquals(RetrofitSyntaxHelper.constructAnnotatedParam(Language.JAVA, "@Field",
                "field", "field"), "@Field(\"field\") String field");

        // Test the header annotation in Kotlin
        Assert.assertEquals(RetrofitSyntaxHelper.constructAnnotatedParam(Language.KOTLIN, "@Field",
                "field", "field"), "@Field(\"field\") field: String");

    }

    @Test
    public void constructAnnotatedParam_query() {

        // Test the field annotation in Java
        Assert.assertEquals(RetrofitSyntaxHelper.constructAnnotatedParam(Language.JAVA, "@Query",
                "query", "query"), "@Query(\"query\") String query");

        // Test the header annotation in Kotlin
        Assert.assertEquals(RetrofitSyntaxHelper.constructAnnotatedParam(Language.KOTLIN, "@Query",
                "query", "query"), "@Query(\"query\") query: String");

    }

    @Test
    public void constructMethodSignature_java() {

        // Java Language
        String dynamicHeader = RetrofitSyntaxHelper.constructAnnotatedParam(Language.JAVA, "@Header",
                "header", "header");
        String field = RetrofitSyntaxHelper.constructAnnotatedParam(Language.JAVA, "@Field",
                "field", "field");
        String query = RetrofitSyntaxHelper.constructAnnotatedParam(Language.JAVA, "@Query",
                "query", "query");

        // Create a method signature without dynamic header, fields or queries
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.JAVA, "Response",
                "GetDemo", "", "", ""),
                "Response GetDemo();");

        // Create a method signature with dynamic header but without fields or queries
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.JAVA, "Response",
                "GetDemo", dynamicHeader, "", ""),
                "Response GetDemo(@Header(\"header\") String header);");

        // Create a method signature with dynamic header, fields but without queries
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.JAVA, "Response",
                "GetDemo", dynamicHeader, field, ""),
                "Response GetDemo(@Header(\"header\") String header, @Field(\"field\") String field);");

        // Create a method signature with dynamic header, fields and queries
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.JAVA, "Response",
                        "GetDemo", dynamicHeader, field, query),
                "Response GetDemo(@Header(\"header\") String header, @Field(\"field\") String field, @Query(\"query\") String query);");

        // Create a method signature without dynamic header but with fields and queries
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.JAVA, "Response",
                        "GetDemo", "", field, query),
                "Response GetDemo(@Field(\"field\") String field, @Query(\"query\") String query);");

        // Create a method signature with fields only
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.JAVA, "Response",
                        "GetDemo", "", field, ""),
                "Response GetDemo(@Field(\"field\") String field);");

        // Create a method signature with queries only
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.JAVA, "Response",
                        "GetDemo", "", "", query),
                "Response GetDemo(@Query(\"query\") String query);");
    }

    @Test
    public void constructMethodSignature_kotlin() {

        // Kotlin Language
        String dynamicHeader = RetrofitSyntaxHelper.constructAnnotatedParam(Language.KOTLIN, "@Header",
                "header", "header");
        String field = RetrofitSyntaxHelper.constructAnnotatedParam(Language.KOTLIN, "@Field",
                "field", "field");
        String query = RetrofitSyntaxHelper.constructAnnotatedParam(Language.KOTLIN, "@Query",
                "query", "query");

        // Create a method signature without dynamic header, fields or queries
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.KOTLIN, "Response",
                        "GetDemo", "", "", ""),
                "fun GetDemo() : Response");

        // Create a method signature with dynamic header but without fields or queries
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.KOTLIN, "Response",
                        "GetDemo", dynamicHeader, "", ""),
                "fun GetDemo(@Header(\"header\") header: String) : Response");

        // Create a method signature with dynamic header, fields but without queries
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.KOTLIN, "Response",
                        "GetDemo", dynamicHeader, field, ""),
                "fun GetDemo(@Header(\"header\") header: String, @Field(\"field\") field: String) : Response");

        // Create a method signature with dynamic header, fields and queries
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.KOTLIN, "Response",
                        "GetDemo", dynamicHeader, field, query),
                "fun GetDemo(@Header(\"header\") header: String, @Field(\"field\") field: String, @Query(\"query\") query: String) : Response");

        // Create a method signature without dynamic header but with fields and queries
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.KOTLIN, "Response",
                        "GetDemo", "", field, query),
                "fun GetDemo(@Field(\"field\") field: String, @Query(\"query\") query: String) : Response");

        // Create a method signature with fields only
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.KOTLIN, "Response",
                        "GetDemo", "", field, ""),
                "fun GetDemo(@Field(\"field\") field: String) : Response");

        // Create a method signature with queries only
        Assert.assertEquals(RetrofitSyntaxHelper.constructMethodSignature(Language.KOTLIN, "Response",
                        "GetDemo", "", "", query),
                "fun GetDemo(@Query(\"query\") query: String) : Response");
    }
}