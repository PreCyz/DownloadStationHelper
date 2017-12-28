package pg.ui.window.controller.task.atomic.call;

import pg.service.FileService;
import pg.service.FileServiceImpl;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-10-29 */
public class UpdateImdbMapCall implements Callable<Void> {

    private final List<TorrentDetail> torrents;

    public UpdateImdbMapCall(List<TorrentDetail> torrents) {
        this.torrents = torrents;
    }

    @Override
    public Void call() throws Exception {
        FileService fileService = new FileServiceImpl();
        fileService.buildImdbMap(torrents);
        fileService.writeImdbMapToFile();
        return null;
    }

}
