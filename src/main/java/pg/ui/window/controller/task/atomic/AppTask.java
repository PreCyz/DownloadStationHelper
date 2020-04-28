package pg.ui.window.controller.task.atomic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pg.exceptions.ProgramException;
import pg.exceptions.UIError;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/** Created by Gawa 2017-10-25 */
public class AppTask<C> {
    private final Logger logger;
    private FutureTask<C> task;

    public AppTask(Callable<C> work, Executor executor) {
        task = new FutureTask<>(work);
        this.logger = LoggerFactory.getLogger(this.getClass());
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
