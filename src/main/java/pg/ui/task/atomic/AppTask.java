package pg.ui.task.atomic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/** Created by Gawa 2017-10-25 */
public class AppTask<C> {
    private final Logger logger;
    private FutureTask<C> task;

    public AppTask(Callable<C> work, Executor executor) {
        task = new FutureTask<>(work);
        this.logger = LogManager.getLogger(this.getClass());
        executor.execute(task);
    }

    public boolean isDone() {
        return task.isDone();
    }

    public C get() {
        try {
            return task.get();
        } catch(Exception e) {
            logger.error("Error when getting result.", e);
            throw new ProgramException(UIError.LAUNCH_PROGRAM);
        }
    }
}
