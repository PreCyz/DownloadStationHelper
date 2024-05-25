package pg.web.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/** Created by Gawa on 15/08/17. */
public class GetClient {

    private static final Logger logger = LoggerFactory.getLogger(GetClient.class);
    private static final HttpClient CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    private static final String USER_AGENT = "Java 17.0.5 Native Http Client";

    private final String url;
    private String output;

    public GetClient(String url) {
        this.url = url;
    }

    public Optional<String> get() {
        return get(Collections.emptyMap());
    }

    public Optional<String> get(Map<String, String> cookies) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .header("User-Agent", USER_AGENT);

        StringBuilder cookieValue = new StringBuilder();
        if (cookies != null && !cookies.isEmpty()) {
            for (Map.Entry<String, String> entry : cookies.entrySet()) {
                cookieValue.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
            }
            builder.header("Cookie", cookieValue.substring(0, cookieValue.lastIndexOf(";")));
        }

        HttpRequest request = builder.build();

        try {
            HttpResponse<InputStream> res = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream content = res.body();
                 BufferedReader rd = new BufferedReader(new InputStreamReader(content))) {

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
        } catch (InterruptedException | IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return Optional.empty();
    }

    private void setOutput(String value) {
        output = value;
    }

    public void downloadFile(String destination) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("User-Agent", USER_AGENT)
                .build();
        try {
            HttpResponse<InputStream> res = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
            logger.info("Response: {} {}", res.version().name(), res.statusCode());
            Files.copy(res.body(), new File(destination).toPath(), StandardCopyOption.REPLACE_EXISTING);
            res.body().close();
        } catch (Exception e) {
            logger.error("Could not download the file", e);
        }
    }
}
