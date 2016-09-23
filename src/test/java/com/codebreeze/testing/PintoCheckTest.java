package com.codebreeze.testing;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class PintoCheckTest
{
    @Test
    public void check_classes_uninstantiable()
    {
        Verify.uninstantiable(PintoCheck.class);
        Verify.uninstantiable(PintoCheck.Argument.class);
        Verify.uninstantiable(PintoCheck.State.class);
    }

    @Test
    public void not_same_should_throw_exception_if_same_instance_passed()
    {
        //given
        final Object o = new Object();

        //when
        final Throwable thrown = catchThrowable(() -> PintoCheck.State.notSame(o, o, () -> "msg"));


        //then
        assertThat(thrown).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void not_same_should_not_throw_exception_if_different_instances_passed()
    {
        //given
        final Object o1 = new Object();
        final Object o2 = new Object();

        //when
        final Throwable thrown = catchThrowable(() -> PintoCheck.State.notSame(o1, o2, () -> "msg"));


        //then
        assertThat(thrown).isNull();
    }

    @Test
    public void not_less_than_should_throw_exception_if_smaller_integer_passed()
    {
        //given
        final Object o = new Object();

        //when
        final Throwable thrown = catchThrowable(() -> PintoCheck.Argument.notLessThan(3, 4, () -> "msg"));


        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void not_less_than_should_not_throw_exception_if_larger_integer_passed()
    {
        //given
        final Object o = new Object();

        //when
        final Throwable thrown = catchThrowable(() -> PintoCheck.Argument.notLessThan(5, 4, () -> "msg"));


        //then
        assertThat(thrown).isNull();
    }

}
