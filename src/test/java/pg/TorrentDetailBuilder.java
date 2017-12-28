package pg;

import pg.web.torrent.TorrentDetail;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**Created by Gawa 2017-09-24*/
public final class TorrentDetailBuilder {

    private TorrentDetail torrentDetail;

    public TorrentDetailBuilder() {
        torrentDetail = mock(TorrentDetail.class);
    }

    public TorrentDetailBuilder withId(int id) {
        when(torrentDetail.getId()).thenReturn(id);
        return this;
    }

    public TorrentDetailBuilder withHash(String hash) {
        when(torrentDetail.getHash()).thenReturn(hash);
        return this;
    }

    public TorrentDetailBuilder withFilename(String filename) {
        when(torrentDetail.getFilename()).thenReturn(filename);
        return this;
    }

    public TorrentDetailBuilder withEpisodeUrl(String episodeUrl) {
        when(torrentDetail.getEpisodeUrl()).thenReturn(episodeUrl);
        return this;
    }

    public TorrentDetailBuilder withTorrentUrl(String torrentUrl) {
        when(torrentDetail.getTorrentUrl()).thenReturn(torrentUrl);
        return this;
    }

    public TorrentDetailBuilder withMagnetUrl(String magnetUrl) {
        when(torrentDetail.getMagnetUrl()).thenReturn(magnetUrl);
        return this;
    }

    public TorrentDetailBuilder withTitle(String title) {
        when(torrentDetail.getTitle()).thenReturn(title);
        return this;
    }

    public TorrentDetailBuilder withImdbId(String imdbId) {
        when(torrentDetail.getImdbId()).thenReturn(imdbId);
        return this;
    }

    public TorrentDetailBuilder withSeason(String season) {
        when(torrentDetail.getSeason()).thenReturn(season);
        return this;
    }

    public TorrentDetailBuilder withEpisode(String episode) {
        when(torrentDetail.getEpisode()).thenReturn(episode);
        return this;
    }

    public TorrentDetailBuilder withSmallScreenshot(String smallScreenshot) {
        when(torrentDetail.getSmallScreenshot()).thenReturn(smallScreenshot);
        return this;
    }

    public TorrentDetailBuilder withLargeScreenshot(String largeScreenshot) {
        when(torrentDetail.getLargeScreenshot()).thenReturn(largeScreenshot);
        return this;
    }

    public TorrentDetailBuilder withSeeds(int seeds) {
        when(torrentDetail.getSeeds()).thenReturn(seeds);
        return this;
    }

    public TorrentDetailBuilder withPeers(int peers) {
        when(torrentDetail.getPeers()).thenReturn(peers);
        return this;
    }

    public TorrentDetailBuilder withDateReleased(long dateReleased) {
        when(torrentDetail.getDateReleased()).thenReturn(dateReleased);
        return this;
    }

    public TorrentDetailBuilder withSize(long size) {
        when(torrentDetail.getSize()).thenReturn(String.valueOf(size));
        return this;
    }

    public TorrentDetail mockTorrentDetail() {
        return torrentDetail;
    }
}
