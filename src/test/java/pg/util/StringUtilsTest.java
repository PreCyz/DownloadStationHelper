package pg.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertThat(StringUtils.nullOrTrimEmpty(" ")).isTrue();
    }

    @Test
    public void givenStringValueWhenNullOrTrimEmptyThenReturnFalse() {
        assertThat(StringUtils.nullOrTrimEmpty("string ")).isFalse();
    }

}