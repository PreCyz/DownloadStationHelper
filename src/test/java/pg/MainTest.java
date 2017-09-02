package pg;

import org.junit.Test;
import pg.web.model.SettingKeys;

import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**Created by Gawa on 27/08/17.*/
public class MainTest {

    @Test
    public void givenNoUsernameAndNullArgsWhenExtractUsernameFromArgsThenExitProgram() {
        try {
            Main.extractUsernameFromArgs(null, new Properties());
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getLocalizedMessage(), is( equalTo("No userName where given. Add username to " +
                    "application.properties or run program with username param (username=login).")));
        }
    }

    @Test
    public void givenNoUsernameWhenExtractUsernameFromArgsThenExitProgram() {
        try {
            Main.extractUsernameFromArgs(new String[]{"someArgument"}, new Properties());
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getLocalizedMessage(), is( equalTo("No userName where given. Add username to " +
                    "application.properties or run program with username param (username=login).")));
        }
    }

    @Test
    public void givenUsernameInArgsWhenExtractUsernameFromArgsThenAddUserNameToProperties() {
        Properties applications = new Properties();
        Main.extractUsernameFromArgs(new String[]{"username=someUserName"}, applications);
        assertThat(applications.containsKey(SettingKeys.USERNAME.key()), is( equalTo(true)));
        assertThat(applications.get(SettingKeys.USERNAME.key()), is( equalTo("someUserName")));
    }

    @Test
    public void givenUsernameInPropertiesWhenExtractUsernameFromArgsThenUserNameInProperties() {
        Properties applications = new Properties();
        applications.setProperty(SettingKeys.USERNAME.key(), "someUserName");
        Main.extractUsernameFromArgs(null, applications);
        assertThat(applications.containsKey(SettingKeys.USERNAME.key()), is( equalTo(true)));
        assertThat(applications.get(SettingKeys.USERNAME.key()), is( equalTo("someUserName")));
    }

    @Test
    public void givenUsernameInArgsAndPropertiesWhenExtractUsernameFromArgsThenUserNameFromProperties() {
        Properties applications = new Properties();
        applications.setProperty(SettingKeys.USERNAME.key(), "userFromProperties");
        Main.extractUsernameFromArgs(new String[]{"username=userFromArgs"}, applications);
        assertThat(applications.containsKey(SettingKeys.USERNAME.key()), is( equalTo(true)));
        assertThat(applications.get(SettingKeys.USERNAME.key()), is( equalTo("userFromProperties")));
    }
}