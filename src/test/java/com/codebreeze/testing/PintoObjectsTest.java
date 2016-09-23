package com.codebreeze.testing;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class PintoObjectsTest
{
    @Test
    public void cannot_be_instantiated()
    {
        Verify.uninstantiable(PintoObjects.class);
    }

    @Test
    public void firstNonNull()
    throws Exception
    {
        assertThat(PintoObjects.firstNonNull(null, "abc")).isEqualTo("abc");
        assertThat(PintoObjects.firstNonNull("abc", null)).isEqualTo("abc");
        assertThat(PintoObjects.firstNonNull(null, (Object)null)).isEqualTo(null);
        assertThat(PintoObjects.firstNonNull("cde", "abc")).isEqualTo("cde");
    }

    @Test
    public void checkNotNull_throws_null_pointer_exception_if_given_null()
    throws Exception
    {
        //given
        final String ns = null;

        //when
        final Throwable throwable = catchThrowable(() -> PintoObjects.checkNotNull(ns));

        //then
        assertThat(throwable).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void checkNotNull_should_return_its_input_other_wise()
    throws Exception
    {
        assertThat(PintoObjects.checkNotNull("abc")).isEqualTo("abc");
    }
}
