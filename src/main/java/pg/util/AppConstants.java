package pg.util;

import pg.web.model.SettingKeys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**Created by Gawa 2017-09-17*/
public final class AppConstants {
    private AppConstants() {}

    public static final String IMDB_FILE_NAME = "imdbTitleMap.json";
    public static String MATCHING_TORRENTS_FILE = "matchTorrents.json";
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String SHOWS_PROPERTIES = "shows.properties";

    public static Path fullFilePath(String fileName) {
        String directoryPath = PropertyLoader.loadApplicationProperties().getProperty(SettingKeys.FILE_PATH.key());
        if (Files.notExists(Paths.get(directoryPath))) {
            try {
                Files.createDirectory(Paths.get(directoryPath));
            } catch(IOException ex) {
                throw new IllegalArgumentException(ex.getLocalizedMessage());
            }
        }
        String filePath = String.format("%s/%s.json", directoryPath, fileName);
        if (fileName.endsWith(".json")) {
            filePath = String.format("%s/%s", directoryPath, fileName);
        }
        return Paths.get(filePath).toAbsolutePath();
    }
}
