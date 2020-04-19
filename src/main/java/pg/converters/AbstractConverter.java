package pg.converters;

import java.util.List;
import java.util.stream.Collectors;

/** Created by Gawa 2018-03-03 */
public abstract class AbstractConverter<S, D> implements Converter<S, D> {

    public abstract D convert(S source);

    public final List<D> convert(List<S> sources) {
        return sources.stream().map(this::convert).collect(Collectors.toList());
    }
}
