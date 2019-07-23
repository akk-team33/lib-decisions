package de.team33.libs.decisions.v1;

import java.util.function.Predicate;

public class Choice<T, R> {

    final Predicate<T> condition;
    final R result;

    public Choice(final Predicate<T> condition, final R result) {
        this.condition = condition;
        this.result = result;
    }
}
