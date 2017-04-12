package hfe.statichtml;

import hfe.testing.EmbeddedTomcatListener;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;

@Listeners(EmbeddedTomcatListener.class)
public class StaticHtmlSiteTest {

    @Test
    public void loadSiteContainingStringMatsInHtmlCode_ThenResponseContainsStringMats() throws ClassNotFoundException, IOException {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpclient.execute(new HttpGet("http://localhost:8080/hfe/mats.html"));
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("mats"));
        response.close();
        httpclient.close();
    }
}
