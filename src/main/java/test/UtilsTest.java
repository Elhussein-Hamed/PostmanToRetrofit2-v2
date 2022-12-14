package test;

import com.hamed.postmantoretrofit2v2.utils.Utils;
import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

    @Test
    public void convertToTitleCase() {
        Assert.assertEquals(Utils.convertToTitleCase("Test Case"), "Test Case");
        Assert.assertEquals(Utils.convertToTitleCase("small case"), "Small Case");
        Assert.assertEquals(Utils.convertToTitleCase("unOrDEreD CAsE"), "Unordered Case");
        Assert.assertEquals(Utils.convertToTitleCase("TitleCase"), "TitleCase");
        Assert.assertEquals(Utils.convertToTitleCase("GET"), "Get");
    }

    @Test
    public void skipQuotes() {
        Assert.assertEquals(Utils.skipQuotes("Header"), "Header");
        Assert.assertEquals(Utils.skipQuotes("Header\"With \"quotes\""), "Header\\\"With \\\"quotes\\\"");
        Assert.assertEquals(Utils.skipQuotes("sec-ch-ua: \"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\""),
                "sec-ch-ua: \\\"Google Chrome\\\";v=\\\"105\\\", \\\"Not)A;Brand\\\";v=\\\"8\\\", \\\"Chromium\\\";v=\\\"105\\\"");
        Assert.assertEquals(Utils.skipQuotes("sec-ch-ua-platform: \"Windows\""), "sec-ch-ua-platform: \\\"Windows\\\"");
    }
}