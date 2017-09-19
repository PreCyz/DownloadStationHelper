package pg.filter.factory;

import pg.filter.AgeFilter;
import pg.filter.FileSizeFilter;
import pg.filter.Filter;
import pg.filter.HistoryFilter;
import pg.loader.ApplicationPropertiesLoader;
import pg.web.model.SettingKeys;

import java.util.HashSet;
import java.util.Set;

/**Created by Pawel Gawedzki on 9/19/2017.*/
public final class FilterFactory {

    private static final int DEFAULT_TORRENT_AGE = 0;
    private static final String DEFAULT_REPEAT_DOWNLOAD = "N";
    private static final String DEFAULT_MAX_FILE_SIZE = "0";

    private FilterFactory() {}

    public static Set<Filter> getFilters() {
        ApplicationPropertiesLoader application = ApplicationPropertiesLoader.getInstance();
        Set<Filter> filters = new HashSet<>();
        if (application.getTorrentAge(DEFAULT_TORRENT_AGE) > 0) {
            filters.add(getFilter(SettingKeys.TORRENT_AGE));
        }
        if (DEFAULT_REPEAT_DOWNLOAD.equalsIgnoreCase(application.getRepeatDownload(DEFAULT_REPEAT_DOWNLOAD))) {
            filters.add(getFilter(SettingKeys.REPEAT_DOWNLOAD));
        }
        if (!DEFAULT_MAX_FILE_SIZE.equals(application.getMaxFileSize(DEFAULT_MAX_FILE_SIZE))) {
            filters.add(getFilter(SettingKeys.MAX_FILE_SIZE));
        }
        return filters;
    }

    private static Filter getFilter(SettingKeys key) {
        ApplicationPropertiesLoader application = ApplicationPropertiesLoader.getInstance();
        switch (key) {
            case TORRENT_AGE:
                int defaultTorrentAge = 0;
                return new AgeFilter(application.getTorrentAge(defaultTorrentAge));
            case REPEAT_DOWNLOAD:
                return new HistoryFilter();
            case MAX_FILE_SIZE:
                String defaultMaxFileSize = "0";
                return new FileSizeFilter(application.getMaxFileSize(defaultMaxFileSize));
        }
        return null;
    }
}
