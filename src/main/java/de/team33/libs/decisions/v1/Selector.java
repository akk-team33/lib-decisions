package de.team33.libs.decisions.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An instrument for creating {@link Function}s that produces predetermined results due to certain input parameter
 * conditions.
 */
public class Selector<T, R> {

    private final List<Entry<T, R>> cases = new LinkedList<>();
    private final Implication implication = new Implication();

    /**
     * Returns a {@link Condition} from a given {@link Predicate} for an expected function parameter.
     */
    public final Condition when(final Predicate<? super T> predicate) {
        return new Condition(predicate);
    }

    private static final class Result<T, R> implements Function<T, R> {

        private final List<Entry<T, R>> cases;
        private final Function<T, R> fallback;

        private Result(final List<Entry<T, R>> cases, final Function<T, R> fallback) {
            this.cases = Collections.unmodifiableList(new ArrayList<>(cases));
            this.fallback = fallback;
        }

        @Override
        public final R apply(final T t) {
            return cases.stream()
                    .filter(entry -> entry.filter.test(t))
                    .findFirst()
                    .map(entry -> entry.result)
                    .orElseGet(() -> fallback.apply(t));
        }
    }

    private static final class Entry<T, R> {

        private final Predicate<? super T> filter;
        private final R result;

        private Entry(final Predicate<? super T> filter, final R result) {
            this.filter = filter;
            this.result = result;
        }
    }

    /**
     * Defines an implication, as it is to be used in this context.
     */
    public final class Implication {

        /**
         * Specifies and returns an alternative {@link Condition} from a given {@link Predicate} for an expected
         * function parameter.
         */
        public final Condition orWhen(final Predicate<? super T> predicate) {
            return new Condition(predicate);
        }

        /**
         * Terminating operation: Specifies a fallback result, completes and returns the resulting {@link Function}.
         */
        public final Function<T, R> orElse(final R fallback) {
            return orElseGet(t -> fallback);
        }

        /**
         * Terminating operation: Specifies how to get a fallback result from an expected function parameter,
         * completes and returns the resulting {@link Function}.
         */
        public Function<T, R> orElseGet(final Function<T, R> fallback) {
            return new Result<>(cases, fallback);
        }

        /**
         * Terminating operation: Specifies how to get a {@link RuntimeException} when no result is available,
         * completes and returns the resulting {@link Function}.
         */
        public <X extends RuntimeException> Function<T, R> orElseThrow(final Function<T, X> newException) {
            return orElseGet(t -> {
                throw newException.apply(t);
            });
        }
    }

    /**
     * Defines a condition, as it is to be used in this context.
     */
    public final class Condition {

        private final Predicate<? super T> predicate;

        private Condition(final Predicate<? super T> predicate) {
            this.predicate = predicate;
        }

        /**
         * Defines and returns an {@link Implication} for this condition.
         */
        public final Implication then(final R result) {
            cases.add(new Entry<>(predicate, result));
            return implication;
        }
    }
}
