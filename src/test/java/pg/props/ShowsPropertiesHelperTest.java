package pg.props;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by Gawa 2017-10-16
 */
public class ShowsPropertiesHelperTest {
    private ShowsPropertiesHelper helper = ShowsPropertiesHelper.getInstance();

    @Test
    public void givenKeyWithNumberWhenExtractIdFromKeyThenReturnNumber() {
        int actual = helper.extractIdFromKey("show.4.baseWords");
        assertThat(actual, is( equalTo(4)));

        actual = helper.extractIdFromKey("show.3.matchPrecision");
        assertThat(actual, is( equalTo(3)));

        actual = helper.extractIdFromKey("show.2.imdbId");
        assertThat(actual, is( equalTo(2)));
    }

    @Test(expected = NumberFormatException.class)
    public void givenKeyWithoutNumberWhenExtractIdFromKeyThenThrowNumberFormatException() {
        int actual = helper.extractIdFromKey("show..baseWords");
        assertThat(actual, is( equalTo(4)));
    }

    @Test
    public void givenKeyWithoutKeyWordWhenExtractIdFromKeyThenReturnZero() {
        int actual = helper.extractIdFromKey("show.1.");
        assertThat(actual, is( equalTo(0)));

        actual = helper.extractIdFromKey("show.1.notKeyWord");
        assertThat(actual, is( equalTo(0)));
    }

    @Test
    public void givenKeyWithoutShowWordWhenExtractIdFromKeyThenReturnNumber() {
        int actual = helper.extractIdFromKey("1.baseWords");
        assertThat(actual, is( equalTo(1)));
    }

}