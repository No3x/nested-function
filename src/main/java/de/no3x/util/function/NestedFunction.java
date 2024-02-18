package de.no3x.util.function;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface NestedFunction<T, R> extends Function<T, R> {

    static <T, R> NestedFunction<T, R> of(Function<T, R> mapper) {
        return (T t) -> mapper.apply(t);
    }

    default <V> NestedFunction<T, V> nested(NestedFunction<R, V> mapper) {
        return (T t) -> Optional.ofNullable(apply(t)).map(mapper).orElse(null);
    }

    default Predicate<T> then(Predicate<R> predicate) {
        return (T t) -> Optional.ofNullable(apply(t)).stream().anyMatch(predicate);
    }

    default <V> Function<T, V> cached(NestedFunction<R, V> mapper) {
        LoadingCache<R, V> cache = CacheBuilder.newBuilder().build(CacheLoader.from(mapper::apply));
        return (T t) -> Optional.ofNullable(apply(t)).map(cache).orElse(null);
    }
}

