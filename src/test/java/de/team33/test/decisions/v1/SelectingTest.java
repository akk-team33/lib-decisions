package de.team33.test.decisions.v1;

import de.team33.libs.decisions.v1.Selecting;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class SelectingTest {

    @Test
    public void whenOrWhenOrElse() {
        final Function<Criterion, Criterion> selector = new Selecting<Criterion, Criterion>()
                .reply(Criterion.ABC).when(Criterion.ABC::equals)
                .orReply(Criterion.DEF).when(Criterion.DEF::equals)
                .orReply(Criterion.GHI).when(Criterion.GHI::equals)
                .orReply(Criterion.JKL).when(Criterion.JKL::equals)
                .orReply(Criterion.MNO).when(Criterion.MNO::equals)
                .orElseReply(null);
        for (final Criterion value : Criterion.values()) {
            assertEquals(value, selector.apply(value));
        }
        assertNull(selector.apply(null));
    }

    @Test
    public void whenOrWhenOrElseGet() {
        final Function<Criterion, Criterion> selector = new Selecting<Criterion, Criterion>()
                .reply(Criterion.ABC).when(Criterion.ABC::equals)
                .orReply(Criterion.DEF).when(Criterion.DEF::equals)
                .orReply(Criterion.GHI).when(Criterion.GHI::equals)
                .orReply(Criterion.JKL).when(Criterion.JKL::equals)
                .orElseGet(t -> t);
        for (final Criterion value : Criterion.values()) {
            assertEquals(value, selector.apply(value));
        }
        assertNull(selector.apply(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void orElseThrow() {
        final Function<Criterion, String> selector = new Selecting<Criterion, String>()
                .reply(Criterion.ABC.name()).when(Criterion.ABC::equals)
                .orReply(Criterion.DEF.name()).when(Criterion.DEF::equals)
                .orReply(Criterion.GHI.name()).when(Criterion.GHI::equals)
                .orElseThrow(criterion -> new IllegalArgumentException("unknown case: " + criterion));
        for (final Criterion value : Criterion.values()) {
            assertEquals(value.name(), selector.apply(value));
        }
        fail("should fail on JKL");
    }

    @Test
    public void cover() {
        final Function<Criterion, String> selector = Selecting.cover(
                new Selecting<Criterion, Function<Criterion, String>>()
                        .reply(Criterion::name).when(Criterion.ABC::equals)
                        .orReply(Criterion::name).when(Criterion.DEF::equals)
                        .orReply(Criterion::name).when(Criterion.GHI::equals)
                        .orElseReply(Criterion::name)
        );
        for (final Criterion value : Criterion.values()) {
            assertEquals(value.name(), selector.apply(value));
        }
    }

    private enum Criterion {
        ABC, DEF, GHI, JKL, MNO
    }
}
