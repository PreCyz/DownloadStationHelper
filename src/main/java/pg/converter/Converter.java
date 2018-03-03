package pg.converter;

import java.util.List;

/** Created by Gawa 2018-03-03 */
public interface Converter<S, D> {

    D convert(S source);
    List<D> convert(List<S> sources);

}
