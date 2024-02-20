package de.no3x.util.function;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a function that allows composing functions and convenience methods.
 *
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 */
public interface NestedFunction<T, R> extends Function<T, R> {

    /**
     * Returns NestedFunction by wrapping a given function.
     *
     * @param mapper the initial function
     * @param <T> the type of the input to the function
     * @param <R> the type of the result of the function
     * @return NestedFunction
     */
    static <T, R> NestedFunction<T, R> of(Function<T, R> mapper) {
        return mapper::apply;
    }

    /**
     * Nest a function
     *
     * @param mapper the new function to be applied to the result of the existing function
     * @param <V> the new type of the result of the new function
     * @return NestedFunction
     */
    default <V> NestedFunction<T, V> nested(NestedFunction<R, V> mapper) {
        return (T t) -> Optional.ofNullable(apply(t)).map(mapper).orElse(null);
    }

    /**
     * Return as predicate on the result of the function
     *
     * @param predicate predicate to wrapped to the result of the existing function
     * @return Predicate
     */
    default Predicate<T> predicate(Predicate<R> predicate) {
        return (T t) -> Optional.ofNullable(apply(t)).stream().anyMatch(predicate);
    }

    /**
     * Return as a cached function on the result of the function
     *
     * @param mapper the function to be cached
     * @param <V> the new type of the result of the new function
     * @return Function as cached Function
     */
    default <V> Function<T, V> cached(NestedFunction<R, V> mapper) {
        LoadingCache<R, V> cache = CacheBuilder.newBuilder().build(CacheLoader.from(mapper::apply));
        return (T t) -> Optional.ofNullable(apply(t)).map(cache).orElse(null);
    }
}

