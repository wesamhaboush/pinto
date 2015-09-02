package com.codebreeze.testing;

import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.assertEquals;

public class CyclicIndexTest {

    @Test(expected = RuntimeException.class)
    public void testConstructorWillRejectZeroOrLessArrays() {
        final int invalidLength = nextInt(-10, 0);
        new CyclicIndex(invalidLength);
    }

    @Test
    public void testNextWorksFor1() {
        //given
        final int length = 1;

        //when
        final CyclicIndex cai = new CyclicIndex(length);

        //then
        assertEquals(newArrayList(0, 0, 0),
                newArrayList(
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
        assertEquals(newArrayList(0, 1, 0, 1, 0, 1),
                newArrayList(
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
        assertEquals(newArrayList(0, 1, 2, 0, 1, 2, 0, 1, 2),
                newArrayList(
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
