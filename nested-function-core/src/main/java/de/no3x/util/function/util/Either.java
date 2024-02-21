package de.no3x.util.function.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * Inspired by FT Either, but I'm sure this is not actually Either.
 * Need to find a better name or move to actual either.
 *
 * @param <A> type for left
 * @param <B> type for right
 */
public class Either<A, B> {
    private final A left;
    private final B right;

    private Either(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public A getLeft() {
        return left;
    }
    public B getRight() {
        return right;
    }

   public static <A, B> Either<A, B> left(A value) {
       return new Either<>(Objects.requireNonNull(value), null);
   }

   public static <A, B> Either<A, B> right(B value) {
       return new Either<>(null, Objects.requireNonNull(value));
   }

    public <R> R fold(boolean isLeft , Function<A, R> leftMapper, Function<B, R> rightMapper) {
        return isLeft ? leftMapper.apply(left) : rightMapper.apply(right);
    }

    public static <A> EitherBuilder.RequireIsTrue<A> builder() {
        return EitherBuilder.builder();
    }

    public static class EitherBuilder {
        @FunctionalInterface
        public interface RequireIsTrue<A> {
            RequireIsFalse<A> isTrue(A isTrue);
        }

        @FunctionalInterface
        public interface RequireIsFalse<A> {
            EitherBuilder.EitherBuilderFinalStage<A> isFalse(A isFalse);
        }

        public static class EitherBuilderFinalStage<A> {
            private final A isTrue;
            private final A isFalse;

            public EitherBuilderFinalStage(A isTrue, A isFalse) {
                this.isTrue = isTrue;
                this.isFalse = isFalse;
            }

            public Either<A, A> build() {
                return new Either<>(isTrue, isFalse);
            }
        }

        public static <A> RequireIsTrue<A> builder() {
            return isTrue -> isFalse -> new EitherBuilderFinalStage<>(isTrue, isFalse);
        }
    }
}
