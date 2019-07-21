package de.team33.test.decisions.v1;

import de.team33.libs.decisions.v1.Selector;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class SelectorTest {

    @Test
    public void whenOrWhenOrElse() {
        final Function<Criterion, Criterion> selector = new Selector<Criterion, Criterion>()
                .when(Criterion.ABC::equals).then(Criterion.ABC)
                .orWhen(Criterion.DEF::equals).then(Criterion.DEF)
                .orWhen(Criterion.GHI::equals).then(Criterion.GHI)
                .orWhen(Criterion.JKL::equals).then(Criterion.JKL)
                .orWhen(Criterion.MNO::equals).then(Criterion.MNO)
                .orElse(null);
        for (final Criterion value : Criterion.values()) {
            assertEquals(value, selector.apply(value));
        }
        assertNull(selector.apply(null));
    }

    @Test
    public void whenOrWhenOrElseGet() {
        final Function<Criterion, Criterion> selector = new Selector<Criterion, Criterion>()
                .when(Criterion.ABC::equals).then(Criterion.ABC)
                .orWhen(Criterion.DEF::equals).then(Criterion.DEF)
                .orWhen(Criterion.GHI::equals).then(Criterion.GHI)
                .orWhen(Criterion.JKL::equals).then(Criterion.JKL)
                .orElseGet(t -> t);
        for (final Criterion value : Criterion.values()) {
            assertEquals(value, selector.apply(value));
        }
        assertNull(selector.apply(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void orElseThrow() {
        final Function<Criterion, String> selector = new Selector<Criterion, String>()
                .when(Criterion.ABC::equals).then(Criterion.ABC.name())
                .orWhen(Criterion.DEF::equals).then(Criterion.DEF.name())
                .orWhen(Criterion.GHI::equals).then(Criterion.GHI.name())
                .orElseThrow(criterion -> new IllegalArgumentException("unknown case: " + criterion));
        for (final Criterion value : Criterion.values()) {
            assertEquals(value.name(), selector.apply(value));
        }
        fail("should fail on JKL");
    }

    private enum Criterion {
        ABC, DEF, GHI, JKL, MNO
    }
}
