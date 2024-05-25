package pg.web.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.util.*;

/**Created by Gawa on 15/08/17.*/
public class PostClient {

    private static final Logger logger = LoggerFactory.getLogger(PostClient.class);
    private static final HttpClient CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    private final String url;
    private String output;

    public PostClient(String url) {
        this.url = url;
    }

    public Optional<String> get() {
        return get(Collections.emptyMap());
    }

    public Optional<String> get(Map<String, String> cookies) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("User-Agent", "Java 17.0.5 Native Http Client")
                .build();
        try {
            HttpResponse<InputStream> res = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

            try (InputStream content = res.body();
                 BufferedReader rd = new BufferedReader(new InputStreamReader(content))
            ) {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                setOutput(result.toString());

            } catch (Exception ex) {
                logger.error("no data.", ex);
            }

            return Optional.of(output);
        } catch (IOException | InterruptedException e) {
            logger.error(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    private void setOutput(String value) {
        output = value;
    }
}
