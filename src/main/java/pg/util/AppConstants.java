package pg.util;

import pg.props.ApplicationPropertiesHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**Created by Gawa 2017-09-17*/
public final class AppConstants {
    private AppConstants() {}

    public static final String RESOURCE_BUNDLE = "bundle.translation";
    public static final String BUNDLE_PATH = "bundle.translation";
    public static final String IMG_RESOURCE_PATH = String.format("img%s", File.separator);
    public static final String CONNECTING_GIF = String.format("%sconnecting.gif", IMG_RESOURCE_PATH);
    public static final String CONNECTED_GIF = String.format("%sconnected.gif", IMG_RESOURCE_PATH);
    public static final String FXML_RESOURCE_PATH = String.format("fxml%s", File.separator);
    public static final String CSS_RESOURCE_PATH = String.format("css%s", File.separator);

    public static final String IMDB_FILE_NAME = "imdbTitleMap.json";
    public static String MATCHING_TORRENTS_FILE = "matchTorrents.json";
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String SHOWS_PROPERTIES = "shows.properties";
    public static final String SETTINGS = "settings";
    public static final String EMPTY_STRING = "";

    public static Path fullFilePath(String fileName) {
        String directoryPath = ApplicationPropertiesHelper.getInstance().getFilePath("");
        if (Files.notExists(Paths.get(directoryPath))) {
            try {
                Files.createDirectory(Paths.get(directoryPath));
            } catch(IOException ex) {
                throw new IllegalArgumentException(ex.getLocalizedMessage());
            }
        }
        String filePath = String.format("%s%s%s.json", directoryPath, File.separator, fileName);
        if (fileName.endsWith(".json")) {
            filePath = String.format("%s%s%s", directoryPath, File.separator, fileName);
        }
        return Paths.get(filePath).toAbsolutePath();
    }
}
