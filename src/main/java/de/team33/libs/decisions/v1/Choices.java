package de.team33.libs.decisions.v1;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class Choices<T, R> {

    private final List<Choice<T, R>> choices = new LinkedList<>();

    public Choices() {
    }

    @SafeVarargs
    public Choices(final Choice<T, R>... choices) {
        this(Arrays.asList(choices));
    }

    public Choices(final Collection<? extends Choice<T, R>> choices) {
        this.choices.addAll(choices);
    }

    public final Choices<T, R> addChoice(final Predicate<T> condition, final R result) {
        return add(new Choice<>(condition, result));
    }

    public final Choices<T, R> add(final Choice<T, R> choice) {
        choices.add(choice);
        return this;
    }

    public final Function<T, R> addFallback(final R result) {
        return addFallback_(ignored -> result);
    }

    public final Function<T, R> addFallback_(final Function<T, R> fallback) {
        return new Result<T, R>(choices, fallback);
    }

    private static class Result<T, R> implements Function<T, R> {

        private final List<Choice<T, R>> choices;
        private final Function<T, R> fallback;

        private Result(final List<Choice<T, R>> choices,
                       final Function<T, R> fallback) {
            this.choices = Collections.unmodifiableList( new ArrayList<>(choices));
            this.fallback = fallback;
        }

        @Override
        public final R apply(final T t) {
            return choices.stream().filter(c -> c.condition.test(t))
                    .map(c -> c.result)
                    .findFirst()
                    .orElseGet(() -> fallback.apply(t));
        }
    }
}
