package pg.loader;

import org.junit.Before;
import org.junit.Test;
import pg.util.AppConstants;
import pg.web.model.SettingKeys;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by Pawel Gawedzki on 9/18/2017.
 */
public class ApplicationPropertiesLoaderTest {

    private ApplicationPropertiesLoader application;

    @Before
    public void setUp() throws Exception {
        application = ApplicationPropertiesLoader.getInstance();
        application.loadApplicationProperties(AppConstants.APPLICATION_PROPERTIES);
    }

    @Test
    public void givenNoUsernameAndNullArgsWhenExtractUsernameFromArgsThenExitProgram() {
        application.getApplicationProperties().remove(SettingKeys.USERNAME.key());
        try {
            application.extractUsername(null);
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getLocalizedMessage(), is( equalTo("No userName where given. Add username to " +
                    "application.properties or run program with username param (username=login).")));
        }
    }

    @Test
    public void givenNoUsernameWhenExtractUsernameFromArgsThenExitProgram() {
        application.getApplicationProperties().remove(SettingKeys.USERNAME.key());
        try {
            application.extractUsername(new String[]{"someArgument"});
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getLocalizedMessage(), is( equalTo("No userName where given. Add username to " +
                    "application.properties or run program with username param (username=login).")));
        }
    }

    @Test
    public void givenUsernameInArgsWhenExtractUsernameFromArgsThenAddUserNameToProperties() {
        application.getApplicationProperties().remove(SettingKeys.USERNAME.key());
        assertThat(application.getUsername(), is(nullValue()) );

        application.extractUsername(new String[]{"username=someUserName"});
        assertThat(application.getUsername(), is( equalTo("someUserName")));
    }

    @Test
    public void givenUsernameInPropertiesWhenExtractUsernameFromArgsThenUserNameInProperties() {
        application.getApplicationProperties().setProperty(SettingKeys.USERNAME.key(), "someUserName");

        application.extractUsername(null);

        assertThat(application.getUsername(), is( equalTo("someUserName")));
    }

    @Test
    public void givenUsernameInArgsAndPropertiesWhenExtractUsernameFromArgsThenUserNameFromProperties() {
        application.getApplicationProperties().setProperty(SettingKeys.USERNAME.key(), "userFromProperties");

        application.extractUsername(new String[]{"username=userFromArgs"});

        assertThat(application.getUsername(), is( equalTo("userFromProperties")));
    }

}