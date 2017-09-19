package pg.filter;

import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.web.model.torrent.TorrentDetail;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**Created by Pawel Gawedzki on 9/19/2017.*/
public class HistoryFilter implements Filter {

    @Override
    public List<TorrentDetail> apply(List<TorrentDetail> torrents) {
        Optional<Map> mapOpt = JsonUtils.convertFromFile(
                AppConstants.fullFilePath(AppConstants.MATCHING_TORRENTS_FILE),
                Map.class
        );
        if (mapOpt.isPresent()) {
            return torrents.stream()
                    .filter(torrentDetail -> !mapOpt.get().containsKey(torrentDetail.getTitle()))
                    .collect(Collectors.toList());
        }
        return torrents;
    }
}
