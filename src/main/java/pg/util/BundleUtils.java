package pg.util;

import pg.exception.ProgramException;
import pg.exception.UIError;

import java.util.Locale;
import java.util.ResourceBundle;

/**Created by Gawa 2017-10-14*/
public final class BundleUtils {

    private BundleUtils() {}

    public static ResourceBundle readBundles() throws ProgramException {
        ResourceBundle bundle = ResourceBundle.getBundle(AppConstants.BUNDLE_PATH);
        if (bundle == null) {
            throw new ProgramException(UIError.LOAD_BUNDLE);
        }
        return bundle;
    }

    public static ResourceBundle readBundles(Locale locale) throws ProgramException {
        ResourceBundle bundle = ResourceBundle.getBundle(AppConstants.BUNDLE_PATH, locale);
        if (bundle == null) {
            throw new ProgramException(UIError.LOAD_BUNDLE);
        }
        return bundle;
    }
}
