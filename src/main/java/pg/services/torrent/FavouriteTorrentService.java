package pg.services.torrent;

import pg.ui.window.controller.completable.UpdatableTask;
import pg.ui.window.controller.task.atomic.call.torrent.GetTorrentsCustomCall;
import pg.web.torrent.TorrentDetail;
import pg.web.torrent.TorrentResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Created by Gawa 2018-01-06 */
public class FavouriteTorrentService extends AbstractTorrentService implements TorrentService {

    public FavouriteTorrentService(UpdatableTask<?> fxTask) {
        super(fxTask);
    }

    @Override
    public List<TorrentDetail> findTorrents() {
        List<GetTorrentsCustomCall> tasks = createGetTorrentsTasks();
        List<TorrentResponse> torrentResponses = executeTasks(tasks);
        return torrentResponses.stream()
                .flatMap(tr -> tr.getTorrents().stream())
                .collect(Collectors.toList());
    }

    protected List<GetTorrentsCustomCall> createGetTorrentsTasks() {
        Integer numberOfPages = getNumberOfPages();
        List<GetTorrentsCustomCall> tasks = new ArrayList<>(numberOfPages);
        for (int page = defaultPage; page <= numberOfPages; page++) {
            tasks.add(new GetTorrentsCustomCall(createUrl(page)));
        }
        return tasks;
    }

    @Override
    protected Integer getNumberOfPages() {
        return application.getPage(defaultPage);
    }
}
