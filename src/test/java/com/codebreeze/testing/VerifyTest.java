package com.codebreeze.testing;

import org.junit.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class VerifyTest
{
    @Test
    public void uninstantiable_should_reject_null_classes_or_suppliers()
    throws Exception
    {
        assertThat(catchThrowable(() -> Verify.uninstantiable((Supplier<? extends Object>) null)))
                .isInstanceOf(NullPointerException.class);
        assertThat(catchThrowable(() -> Verify.uninstantiable((Class<? extends Object>) null)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void uninstantiable_should_fail_for_instantiable_class()
    throws Exception
    {
        assertThat(catchThrowable(() -> Verify.uninstantiable(Instantiable.class)))
                .isInstanceOf(AssertionError.class);
        assertThat(catchThrowable(() -> Verify.uninstantiable(() -> new Instantiable())))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void uninstantiable_should_succeed_for_uninstantiable_class()
    throws Exception
    {
        assertThat(catchThrowable(() -> Verify.uninstantiable(Verify.class)))
                .isNull();
        assertThat(catchThrowable(() -> Verify.uninstantiable(Uninstantiable.class)))
                .isNull();
        assertThat(catchThrowable(() -> Verify.uninstantiable(() -> new Uninstantiable())))
                .isNull();
    }

    @Test
    public void uninstantiable_should_throw_illegal_argument_exception_if_zero_args_constructor_not_found()
    throws Exception
    {
        assertThat(catchThrowable(() -> Verify.uninstantiable(NoZeroArgConstructorUninstantiable.class)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void useful_should_pass_good_enums()
    throws Exception
    {
        Verify.useful(A.class);
    }

    @Test
    public void throws_null_pointer_exception_if_null_class_is_given()
    throws Exception
    {
        assertThat(catchThrowable(() -> Verify.useful(null)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void throws_null_pointer_exception_if_no_members_enum_is_provided()
    throws Exception
    {
        assertThat(catchThrowable(() -> Verify.useful(C.class)))
                .isInstanceOf(AssertionError.class);
    }

    private static class Instantiable {}
    private static class Uninstantiable {
        private Uninstantiable()
        {
            throw new UnsupportedOperationException("cannot instantiate this class");
        }
    }

    private static class NoZeroArgConstructorUninstantiable {
        private final Integer something;
        private NoZeroArgConstructorUninstantiable(final Integer something)
        {
            this.something = something;
        }
    }

    enum A {B}
    enum C {}
}
