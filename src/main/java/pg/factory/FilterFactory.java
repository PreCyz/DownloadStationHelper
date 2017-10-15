package pg.factory;

import pg.filter.*;
import pg.props.ApplicationPropertiesHelper;
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
        ApplicationPropertiesHelper application = ApplicationPropertiesHelper.getInstance();
        Set<Filter> filters = new HashSet<>();
        if (application.getTorrentAge(DEFAULT_TORRENT_AGE) > 0) {
            filters.add(getFilter(SettingKeys.TORRENT_AGE_DAYS));
        }
        if (DEFAULT_REPEAT_DOWNLOAD.equalsIgnoreCase(application.getRepeatDownload(DEFAULT_REPEAT_DOWNLOAD))) {
            filters.add(getFilter(SettingKeys.REPEAT_DOWNLOAD));
        }
        if (!DEFAULT_MAX_FILE_SIZE.equals(application.getMaxFileSize(DEFAULT_MAX_FILE_SIZE))) {
            filters.add(getFilter(SettingKeys.MAX_FILE_SIZE));
        }
        if (!"".equals(application.getTorrentReleaseDate())) {
            filters.add(getFilter(SettingKeys.TORRENT_RELEASE_DATE));
        }
        return filters;
    }

    private static Filter getFilter(SettingKeys key) {
        ApplicationPropertiesHelper application = ApplicationPropertiesHelper.getInstance();
        switch (key) {
            case TORRENT_AGE_DAYS:
                int defaultTorrentAge = 0;
                return new AgeInDaysFilter(application.getTorrentAge(defaultTorrentAge));
            case REPEAT_DOWNLOAD:
                return new HistoryFilter();
            case MAX_FILE_SIZE:
                String defaultMaxFileSize = "0";
                return new FileSizeFilter(application.getMaxFileSize(defaultMaxFileSize));
            case TORRENT_RELEASE_DATE:
                return new ReleaseDateFilter(application.getTorrentReleaseDate());

        }
        return null;
    }
}
