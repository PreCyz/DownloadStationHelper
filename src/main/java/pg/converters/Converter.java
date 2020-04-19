package pg.converters;

/** Created by Gawa 2018-03-03 */
@FunctionalInterface
public interface Converter<S, D> {

    D convert(S source);

}
