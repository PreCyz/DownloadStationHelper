package pg.service.torrent.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/** Created by Gawa 2017-10-25 */
class GetTorrentsTask {
    private final Logger logger;
    private GetTorrents work;
    private FutureTask<String> task;
    private String url;

    GetTorrentsTask(String url, Executor executor) {
        this.url = url;
        work = new GetTorrents(url);
        task = new FutureTask<>(work);
        this.logger = LogManager.getLogger(this.getClass());
        executor.execute(task);
    }
    String getRequestUrl() {
        return work.getUrl();
    }
    boolean isDone() {
        return task.isDone();
    }

    String getResponse() {
        try {
            return task.get();
        } catch(Exception e) {
            logger.error("Error when getting response {}", url, e);
            throw new RuntimeException(e);
        }
    }
}
