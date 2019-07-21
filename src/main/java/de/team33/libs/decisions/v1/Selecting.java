package de.team33.libs.decisions.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Selecting<T, R> {

    private final List<Entry<T, R>> entries = new LinkedList<>();
    private final Alternative alternative = new Alternative();

    public static <T, R> Function<T, R> cover(final Function<T, ? extends Function<? super T, ? extends R>> stage) {
        return t -> stage.apply(t).apply(t);
    }

    public final Choice reply(final R result) {
        return alternative.orReply(result);
    }

    private static class Entry<T, R> {

        private final Predicate<? super T> condition;
        private final R result;

        private Entry(final Predicate<? super T> condition, final R result) {
            this.condition = condition;
            this.result = result;
        }
    }

    private static class Result<T, R> implements Function<T, R> {

        private final List<Entry<T, R>> entries;
        private final Function<T, R> fallback;

        private Result(final List<Entry<T, R>> entries, final Function<T, R> fallback) {
            this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
            this.fallback = fallback;
        }

        @Override
        public final R apply(final T t) {
            return entries.stream()
                    .filter(entry -> entry.condition.test(t))
                    .map(entry -> entry.result)
                    .findFirst()
                    .orElseGet(() -> fallback.apply(t));
        }
    }

    public class Choice {

        private final R result;

        private Choice(final R result) {
            this.result = result;
        }

        public final Alternative when(final Predicate<? super T> condition) {
            entries.add(new Entry<>(condition, result));
            return alternative;
        }
    }

    public class Alternative {

        public final Choice orReply(final R result) {
            return new Choice(result);
        }

        public final Function<T, R> orElseReply(final R fallback) {
            return orElseGet(t -> fallback);
        }

        public final Function<T, R> orElseGet(final Function<T, R> fallback) {
            return new Result<>(entries, fallback);
        }

        public Function<T, R> orElseThrow(final Function<T, RuntimeException> newException) {
            return orElseGet(t -> {
                throw newException.apply(t);
            });
        }
    }
}
