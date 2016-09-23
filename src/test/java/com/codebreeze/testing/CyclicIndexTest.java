package com.codebreeze.testing;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;

public class CyclicIndexTest {

    @Test
    public void testConstructorWillRejectZeroOrLessArrays() {
        final int invalidLength = -1;
        assertThat(catchThrowable(() -> new CyclicIndex(invalidLength)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConstructorWillRejectZero() {
        final int invalidLength = 0;
        assertThat(catchThrowable(() -> new CyclicIndex(invalidLength)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testNextWorksFor1() {
        //given
        final int length = 1;

        //when
        final CyclicIndex cai = new CyclicIndex(length);

        //then
        assertEquals(asList(0, 0, 0),
                asList(
                cai.next(),
                cai.next(),
                cai.next()));
    }

    @Test
    public void testNextWorksFor2() {
        //given
        final int length = 2;

        //when
        final CyclicIndex cai = new CyclicIndex(length);

        //then
        assertEquals(asList(0, 1, 0, 1, 0, 1),
                asList(
                cai.next(),
                cai.next(),
                cai.next(),
                cai.next(),
                cai.next(),
                cai.next()));
    }

    @Test
    public void testNextWorksFor3() {
        //given
        final int length = 3;

        //when
        final CyclicIndex cai = new CyclicIndex(length);

        //then
        assertEquals(asList(0, 1, 2, 0, 1, 2, 0, 1, 2),
                asList(
                cai.next(),
                cai.next(),
                cai.next(),
                cai.next(),
                cai.next(),
                cai.next(),
                cai.next(),
                cai.next(),
                cai.next()));
    }
}
