package com.codebreeze.testing;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class PintoThrowablesTest
{
    @Test
    public void propagates_runtime_exceptions_as_is()
    throws Exception
    {
        //given

        //when
        final Throwable propagated = catchThrowable(
                () -> PintoThrowables.propagate(new RuntimeException("test run time exception"))
                                                  );

        //then
        assertThat(propagated).isInstanceOf(RuntimeException.class).hasCause(null);
    }

    @Test
    public void propagates_exceptions_wrapped_in_runtime_exceptions()
    throws Exception
    {
        //given

        //when
        final Throwable propagated = catchThrowable(
                () ->
                {
                    throw PintoThrowables.propagate(new Exception("test run time exception"));
                }
                                                   );

        //then
        assertThat(propagated).isInstanceOf(RuntimeException.class)
                              .hasCauseInstanceOf(Exception.class);
    }

    @Test
    public void propagates_errors_as_is()
    throws Exception
    {
        //given

        //when
        final Throwable propagated = catchThrowable(
                () -> PintoThrowables.propagate(new Error("test run time exception"))
                                                   );

        //then
        assertThat(propagated).isInstanceOf(Error.class).hasCause(null);
    }

    @Test
    public void propagate_if_instance_of_should_not_propagate_if_null_given_as_type()
    throws Exception
    {
        //given

        //when
        final Throwable propagated = catchThrowable(
                () -> PintoThrowables.propagateIfInstanceOf(null, Error.class)
                                                   );

        //then
        assertThat(propagated).isNull();
    }

    @Test
    public void propagate_should_re_throw_exception_from_callable_wrapped_in_runtime_exception()
    throws Exception
    {
        //given
        final class TestException extends Exception{}
        //when
        final Throwable propagated = catchThrowable(
                () -> PintoThrowables.propagate(() -> { throw new TestException(); })
                                                   );

        //then
        assertThat(propagated).isInstanceOf(RuntimeException.class).hasCauseInstanceOf(TestException.class);
    }

    @Test
    public void uninstantiable()
    throws Exception
    {
        Verify.uninstantiable(PintoThrowables.class);
    }
}
