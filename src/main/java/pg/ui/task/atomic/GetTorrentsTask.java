package pg.ui.task.atomic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;
import pg.ui.task.atomic.call.GetTorrentsCall;

import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/** Created by Gawa 2017-10-25 */
public class GetTorrentsTask {
    private final Logger logger;
    private GetTorrentsCall getTorrentsCall;
    private FutureTask<String> task;
    private String url;

    public GetTorrentsTask(String url, Executor executor) {
        this.url = url;
        getTorrentsCall = new GetTorrentsCall(url);
        task = new FutureTask<>(getTorrentsCall);
        this.logger = LogManager.getLogger(this.getClass());
        executor.execute(task);
    }
    public String getRequestUrl() {
        return getTorrentsCall.getUrl();
    }

    public boolean isDone() {
        return task.isDone();
    }

    public String getResponse() {
        try {
            return task.get();
        } catch (Exception e) {
            logger.error("Error when getting response for {}", url, e);
            throw new ProgramException(UIError.GET_TORRENTS, e);
        }
    }
}
