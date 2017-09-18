package pg.loader;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import pg.Main;
import pg.web.model.SettingKeys;

import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

/**
 * Created by Pawel Gawedzki on 9/18/2017.
 */
public class ApplicationPropertiesLoaderTest {

    private ApplicationPropertiesLoader application = ApplicationPropertiesLoader.getInstance();

    @Test
    public void givenNoUsernameAndNullArgsWhenExtractUsernameFromArgsThenExitProgram() {
        try {
            application.extractUsernameFromArgs(null);
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getLocalizedMessage(), is( equalTo("No userName where given. Add username to " +
                    "application.properties or run program with username param (username=login).")));
        }
    }

    @Test
    public void givenNoUsernameWhenExtractUsernameFromArgsThenExitProgram() {
        try {
            application.extractUsernameFromArgs(new String[]{"someArgument"});
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getLocalizedMessage(), is( equalTo("No userName where given. Add username to " +
                    "application.properties or run program with username param (username=login).")));
        }
    }

    @Test
    public void givenUsernameInArgsWhenExtractUsernameFromArgsThenAddUserNameToProperties() {
        application.extractUsernameFromArgs(new String[]{"username=someUserName"});
        assertThat(application.getUsername(), is( equalTo("someUserName")));
    }

    @Test
    @Ignore
    public void givenUsernameInPropertiesWhenExtractUsernameFromArgsThenUserNameInProperties() {
        Properties applications = new Properties();
        applications.setProperty(SettingKeys.USERNAME.key(), "someUserName");
        //Main.extractUsernameFromArgs(null, applications);
        assertThat(applications.containsKey(SettingKeys.USERNAME.key()), is( equalTo(true)));
        assertThat(applications.get(SettingKeys.USERNAME.key()), is( equalTo("someUserName")));
    }

    @Test
    @Ignore
    public void givenUsernameInArgsAndPropertiesWhenExtractUsernameFromArgsThenUserNameFromProperties() {
        Properties applications = new Properties();
        applications.setProperty(SettingKeys.USERNAME.key(), "userFromProperties");
        //Main.extractUsernameFromArgs(new String[]{"username=userFromArgs"}, applications);
        assertThat(applications.containsKey(SettingKeys.USERNAME.key()), is( equalTo(true)));
        assertThat(applications.get(SettingKeys.USERNAME.key()), is( equalTo("userFromProperties")));
    }

}