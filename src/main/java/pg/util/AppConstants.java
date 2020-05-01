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
    public static final String IMG_RESOURCE_PATH = "img/";
    public static final String FXML_RESOURCE_PATH = "fxml/";
    public static final String CSS_RESOURCE_PATH = "css/";
    public static final String CONNECTING_GIF = String.format("%sconnecting.gif", IMG_RESOURCE_PATH);
    public static final String CONNECTED_GIF = String.format("%sconnected.gif", IMG_RESOURCE_PATH);
    public static final String CHECK_GIF = String.format("%scheck.gif", IMG_RESOURCE_PATH);
    public static final String FINGER_UP_GIF = String.format("%sfingerUp.gif", IMG_RESOURCE_PATH);
    public static final String EXPLOSION_GIF = String.format("%sexplosion.gif", IMG_RESOURCE_PATH);
    public static final String DELETE_PNG = String.format("%sdelete.png", IMG_RESOURCE_PATH);
    public static final String ADD_PNG = String.format("%sadd.png", IMG_RESOURCE_PATH);
    public static final String CLEAN_PNG = String.format("%sclean.png", IMG_RESOURCE_PATH);
    public static final String DOWNLOAD_ARROW_PNG = String.format("%sdownloadButton.png", IMG_RESOURCE_PATH);
    public static final String SEARCH_LOOP_PNG = String.format("%ssearchButton.png", IMG_RESOURCE_PATH);

    public static final String IMDB_FILE_NAME = "imdbTitleMap.json";
    public static String MATCHING_TORRENTS_FILE = "matchTorrents.json";
    public static final String APPLICATION_PROPERTIES = "application.properties";
    public static final String SHOWS_PROPERTIES = "shows.properties";
    public static final String VERSION_TXT = "version.txt";
    public static final String SETTINGS = "settings";
    public static final String EMPTY_STRING = "";

    public static final String SHOWS_JSON = "shows.json";
    public static final Path SHOWS_PROPERTIES_PATH = Paths.get(".", SETTINGS, SHOWS_PROPERTIES);
    public static final Path SHOWS_JSON_PATH = Paths.get(".", SETTINGS, SHOWS_JSON);
    public static final Path VERSION_PATH = Paths.get(".", SETTINGS, VERSION_TXT);

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
