package hfe.staticc;

import hfe.testing.EmbeddedTomcatListener;
import hfe.tools.HfeUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.logging.Logger;

import static org.testng.Assert.assertEquals;

@Listeners(EmbeddedTomcatListener.class)
public class StaticHtmlSiteTest {

    @Test
    public void loadSiteContainingStringMatsInHtmlCode_ThenResponseContainsStringMats() throws Exception {
        HfeUtils.runWithHtmlSite("build/war_exploded/call.html", null, "mats",
            () -> execute(new HttpGet("http://localhost:8080/hfe/call.html"),
                    response -> assertEquals(EntityUtils.toString(response.getEntity()), "mats")));
    }

    @Test
    public void loadExisintgSite_ThenStatusCodeIs200() throws Exception {
        HfeUtils.runWithHtmlSite("build/war_exploded/call200.html", null, "mats",
                () -> execute(new HttpGet("http://localhost:8080/hfe/call200.html"),
                        response -> assertEquals(response.getStatusLine().getStatusCode(), 200)));
    }

    @Test
    public void loadNotExisintgSite_ThenStatusCodeIs404() throws Exception {
        execute(new HttpGet("http://localhost:8080/hfe/nichtda.html"),
                response -> assertEquals(response.getStatusLine().getStatusCode(), 404));
    }

    @Test
    public void login() {
        execute(new HttpGet("http://localhost:8080/hfe/login.xhtml"), response -> {
            Logger.getLogger("me").info(EntityUtils.toString(response.getEntity()));
            assertEquals(response.getStatusLine().getStatusCode(), 200);
        });
    }

    private void execute(HttpUriRequest request, ExceptionConsumer<CloseableHttpResponse> assertRunnable) {
        CloseableHttpClient httpclient = HttpClientBuilder.create().build();
        try {
            CloseableHttpResponse response = httpclient.execute(request);
            assertRunnable.accept(response);
            response.close();
            httpclient.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    interface ExceptionConsumer<T> {
        void accept(T obj) throws Exception;
    }
}
