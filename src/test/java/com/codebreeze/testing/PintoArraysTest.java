package com.codebreeze.testing;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PintoArraysTest
{
    @Test
    public void cannot_be_instantiated()
    {
        Verify.uninstantiable(PintoArrays.class);
    }


    @Test
    public void toPrimitive_Ints()
    throws Exception
    {
        final Integer[] nullInts = null;
        final Integer[] emptyInts = {};
        assertThat(PintoArrays.toPrimitive(nullInts)).isEqualTo(null);
        assertThat(PintoArrays.toPrimitive(emptyInts)).isEqualTo(new int[0]);
        assertThat(PintoArrays.toPrimitive(new Integer[]{1, 2, 3}))
                .isEqualTo(new int[]{1, 2, 3});
    }

    @Test
    public void toPrimitive_Shorts()
    throws Exception
    {
        final Short[] nullShorts = null;
        final Short[] emptyShorts = {};
        assertThat(PintoArrays.toPrimitive(nullShorts)).isEqualTo(null);
        assertThat(PintoArrays.toPrimitive(emptyShorts)).isEqualTo(new short[0]);
        assertThat(PintoArrays.toPrimitive(new Short[]{1, 2, 3}))
                .isEqualTo(new short[]{1, 2, 3});
    }

    @Test
    public void toPrimitive_Longs()
    throws Exception
    {
        final Long[] nullLongs = null;
        final Long[] emptyLongs = {};
        assertThat(PintoArrays.toPrimitive(nullLongs)).isEqualTo(null);
        assertThat(PintoArrays.toPrimitive(emptyLongs)).isEqualTo(new long[0]);
        assertThat(PintoArrays.toPrimitive(new Long[]{1L, 2L, 3L}))
                .isEqualTo(new long[]{1L, 2L, 3L});
    }

    @Test
    public void toPrimitive_Doubles()
    throws Exception
    {
        final Double[] nullDoubles = null;
        final Double[] emptyDoubles = {};
        assertThat(PintoArrays.toPrimitive(nullDoubles)).isEqualTo(null);
        assertThat(PintoArrays.toPrimitive(emptyDoubles)).isEqualTo(new double[0]);
        assertThat(PintoArrays.toPrimitive(new Double[]{1.0, 2.0, 3.0}))
                .isEqualTo(new double[]{1.0, 2.0, 3.0});
    }

    @Test
    public void toPrimitive_Floats()
    throws Exception
    {
        final Float[] nullFloats = null;
        final Float[] emptyFloats = {};
        assertThat(PintoArrays.toPrimitive(nullFloats)).isEqualTo(null);
        assertThat(PintoArrays.toPrimitive(emptyFloats)).isEqualTo(new float[0]);
        assertThat(PintoArrays.toPrimitive(new Float[]{1.0f, 2.0f, 3.0f}))
                .isEqualTo(new float[]{1.0f, 2.0f, 3.0f});
    }

    @Test
    public void toPrimitive_Byte()
    throws Exception
    {
        final Byte[] nullBytes = null;
        final Byte[] emptyBytes = {};
        assertThat(PintoArrays.toPrimitive(nullBytes)).isEqualTo(null);
        assertThat(PintoArrays.toPrimitive(emptyBytes)).isEqualTo(new byte[0]);
        assertThat(PintoArrays.toPrimitive(new Byte[]{1, 2, 3}))
                .isEqualTo(new byte[]{1, 2, 3});
    }

    @Test
    public void toPrimitive_Characters()
    throws Exception
    {
        final Character[] nullCharacters = null;
        final Character[] emptyCharaters = {};
        assertThat(PintoArrays.toPrimitive(nullCharacters)).isEqualTo(null);
        assertThat(PintoArrays.toPrimitive(emptyCharaters)).isEqualTo(new char[0]);
        assertThat(PintoArrays.toPrimitive(new Character[]{1, 2, 3}))
                .isEqualTo(new char[]{1, 2, 3});
    }

    @Test
    public void toPrimitive_Boolean()
    throws Exception
    {
        final Boolean[] nullBooleans = null;
        final Boolean[] emptyBooleans = {};
        assertThat(PintoArrays.toPrimitive(nullBooleans)).isEqualTo(null);
        assertThat(PintoArrays.toPrimitive(emptyBooleans)).isEqualTo(new boolean[0]);
        assertThat(PintoArrays.toPrimitive(new Boolean[]{true, false, true}))
                .isEqualTo(new boolean[]{true, false, true});
    }
}
