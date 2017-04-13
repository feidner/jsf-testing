package hfe.staticc;

import hfe.testing.EmbeddedTomcatListener;
import hfe.tools.HfeUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

@Listeners( { EmbeddedTomcatListener.class })
public class StaticHtmlSiteTest {

    @Test
    public void loadSiteContainingStringMatsInHtmlCode_ThenResponseContainsStringMats() throws Exception {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = HfeUtils.runWithHtmlSite("build/war_exploded/callme.html", null, "mats",
                () -> httpclient.execute(new HttpGet("http://localhost:8080/hfe/callme.html")));
        assertEquals(EntityUtils.toString(response.getEntity()), "mats");
        response.close();
        httpclient.close();
    }
}
