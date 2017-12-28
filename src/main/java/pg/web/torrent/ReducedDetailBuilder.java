package pg.web.torrent;

import java.util.Date;

/** Created by Gawa 2017-09-14 */
public final class ReducedDetailBuilder {

    private String title;
    private int matchPrecision;
    private Date dateReleased;
    private String magnetUrl;
    private String torrentUrl;
    private String imdbId;
    private String season;
    private String episode;

    private ReducedDetailBuilder() {}

    public static ReducedDetailBuilder newInstance() {
        return new ReducedDetailBuilder();
    }

    public ReducedDetailBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ReducedDetailBuilder withMatchPrecision(int matchPrecision) {
        this.matchPrecision = matchPrecision;
        return this;
    }

    public ReducedDetailBuilder withDateReleased(Date dateReleased) {
        this.dateReleased = dateReleased;
        return this;
    }

    public ReducedDetailBuilder withMagnetUrl(String magnetUrl) {
        this.magnetUrl = magnetUrl;
        return this;
    }

    public ReducedDetailBuilder withTorrentUrl(String torrentUrl) {
        this.torrentUrl = torrentUrl;
        return this;
    }

    public ReducedDetailBuilder withImdbId(String imdbId) {
        this.imdbId = imdbId;
        return this;
    }

    public ReducedDetailBuilder withSeason(String season) {
        this.season = season;
        return this;
    }

    public ReducedDetailBuilder withEpisode(String episode) {
        this.episode = episode;
        return this;
    }

    public ReducedDetail create() {
        return new ReducedDetail(
                title,
                matchPrecision,
                dateReleased,
                magnetUrl,
                torrentUrl,
                imdbId,
                season,
                episode
        );
    }
}
