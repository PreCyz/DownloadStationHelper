package pg.ui.window.controller.task.atomic.call.torrent;

import pg.services.FileService;
import pg.services.FileServiceImpl;
import pg.web.torrent.TorrentDetail;

import java.util.List;
import java.util.concurrent.Callable;

/** Created by Gawa 2017-10-29 */
public class UpdateImdbMapCall implements Callable<Integer> {

    private final List<TorrentDetail> torrents;

    public UpdateImdbMapCall(List<TorrentDetail> torrents) {
        this.torrents = torrents;
    }

    @Override
    public Integer call() {
        FileService fileService = new FileServiceImpl();
        fileService.buildImdbMap(torrents);
        fileService.writeImdbMapToFile();
        return fileService.getImdbMapSize();
    }

}
