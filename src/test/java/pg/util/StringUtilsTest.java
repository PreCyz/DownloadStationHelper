package pg.util;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**Created by Gawa 2017-09-16*/
public class StringUtilsTest {

    @Test
    public void givenNullValueWhenNullOrTrimEmptyThenReturnTrue() {
        assertTrue(StringUtils.nullOrTrimEmpty(null));
    }

    @Test
    public void givenEmptyStringValueWhenNullOrTrimEmptyThenReturnTrue() {
        assertTrue(StringUtils.nullOrTrimEmpty(""));
    }

    @Test
    public void givenSpaceStringValueWhenNullOrTrimEmptyThenReturnTrue() {
        assertThat(StringUtils.nullOrTrimEmpty(" "), is( equalTo(true)));
    }

    @Test
    public void givenStringValueWhenNullOrTrimEmptyThenReturnFalse() {
        assertThat(StringUtils.nullOrTrimEmpty("string "), is( equalTo(false)));
    }

}