package pg.ui.window.controller.completable;

import javafx.concurrent.Task;

/** Created by Gawa 2018-01-06 */
public abstract class UpdatableTask<V> extends Task<V> {
    public abstract void updateProgressTo30(double workDone);
}
