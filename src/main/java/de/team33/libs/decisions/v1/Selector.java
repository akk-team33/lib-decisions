package de.team33.libs.decisions.v1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

/**
 * An instrument for creating {@link Function}s that produces predetermined results due to certain input parameter
 * conditions.
 */
public class Selector<T, R> {

    private final List<Choice<T, R>> choices = new LinkedList<>();
    private final Conclusion conclusion = new Conclusion();

    public Selector() {
    }

    private Selector(final Stream<? extends Choice<T, R>> choices) {
        choices.forEach(this.choices::add);
    }

    /**
     * Prepares a {@link Selector.Conclusion} and returns it.
     */
    @SafeVarargs
    public static <T, R> Selector<T, R>.Conclusion prepare(final Choice<T, R>... choices) {
        return prepare(Stream.of(choices));
    }

    /**
     * Prepares a {@link Selector.Conclusion} and returns it.
     */
    public static <T, R> Selector<T, R>.Conclusion prepare(final Stream<? extends Choice<T, R>> choices) {
        return new Selector<>(choices).conclusion;
    }

    /**
     * Returns a {@link Condition} from a given {@link Predicate} for an expected function parameter.
     */
    public final Condition when(final Predicate<? super T> predicate) {
        return new Condition(predicate);
    }

    private static final class Result<T, R> implements Function<T, R> {

        private final List<Choice<T, R>> choices;
        private final Function<T, R> fallback;

        private Result(final List<Choice<T, R>> choices, final Function<T, R> fallback) {
            this.choices = unmodifiableList(new ArrayList<>(choices));
            this.fallback = fallback;
        }

        @Override
        public final R apply(final T t) {
            return choices.stream()
                    .filter(choice -> choice.condition.test(t))
                    .findFirst()
                    .map(entry -> entry.result)
                    .orElseGet(() -> fallback.apply(t));
        }
    }

    /**
     * Defines a conclusion, as it is to be used in this context.
     */
    public final class Conclusion {

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
            return new Result<>(choices, fallback);
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
         * Defines and returns an {@link Conclusion} for this condition.
         */
        public final Conclusion then(final R result) {
            choices.add(new Choice<>(predicate, result));
            return conclusion;
        }
    }
}
