package de.team33.libs.decisions.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Selector<T, R> {

    private final List<Entry<T, R>> cases = new LinkedList<>();
    private final Successor successor = new Successor();

    public final Case when(final Predicate<? super T> predicate) {
        return new Case(predicate);
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

    public final class Successor {

        public final Case orWhen(final Predicate<? super T> predicate) {
            return new Case(predicate);
        }

        public final Function<T, R> orElse(final R fallback) {
            return orElseGet(t -> fallback);
        }

        public Function<T, R> orElseGet(final Function<T, R> fallback) {
            return new Result<>(cases, fallback);
        }

        public <X extends RuntimeException> Function<T, R> orElseThrow(final Function<T, X> newException) {
            return orElseGet(t -> {
                throw newException.apply(t);
            });
        }
    }

    public final class Case {

        private final Predicate<? super T> predicate;

        private Case(final Predicate<? super T> predicate) {
            this.predicate = predicate;
        }

        public final Selector<T, R>.Successor then(final R result) {
            cases.add(new Entry<>(predicate, result));
            return successor;
        }
    }
}
