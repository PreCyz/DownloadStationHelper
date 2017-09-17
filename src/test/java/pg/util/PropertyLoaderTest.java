package pg.util;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

public class PropertyLoaderTest {

    @Test
    public void whenLoadApplicationPropertiesThenReturnProperties() {
        assertThat(PropertyLoader.loadApplicationProperties(), notNullValue());
    }

    @Test
    public void whenLoadShowsPropertiesThenReturnProperties() {
        assertThat(PropertyLoader.loadShowsProperties(), notNullValue());
    }
}