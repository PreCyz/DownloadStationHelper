package pg.web.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.apache.http.HttpHeaders.USER_AGENT;

/**Created by Gawa on 15/08/17.*/
public class GetClient {

    private final String url;
    private int responseCode;

    public GetClient(String url) {
        this.url = url;
    }

    public Optional<String> get() {
        return get(Collections.emptyMap());
    }

    public Optional<String> get(Map<String, String> cookies) {
        try {
            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);

            // add request header
            request.addHeader("User-Agent", USER_AGENT);
            addCookies(request, cookies);
            HttpResponse response = client.execute(request);

            responseCode = response.getStatusLine().getStatusCode();

            InputStream content = response.getEntity().getContent();
            BufferedReader rd = new BufferedReader(new InputStreamReader(content));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            return Optional.of(result.toString());

        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    private void addCookies(HttpGet request, Map<String, String> cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            StringBuilder value = new StringBuilder();
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                value.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
            }
            request.addHeader("Cookie", value.substring(0, value.lastIndexOf(";")));
        }
    }

    public int getResponseCode() {
        return responseCode;
    }
}
