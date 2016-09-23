package com.codebreeze.testing;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ToStringTesterTest {

    @Test(expected=RuntimeException.class)
    public void testRunAllTestsWithNonConformingNull() {
        ToStringTester.forInstances(new NonConformingWithNull()).verify();
    }

    @Test(expected=RuntimeException.class)
    public void testRunAllTestsWithNonConformingDefault() {
        ToStringTester.forInstances(new NonConformingWithDefault()).verify();
    }

    @Test(expected=RuntimeException.class)
    public void testRunAllTestsWithNonConformingEmpty() {
        ToStringTester.forInstances(new NonConformingWithEmpty()).verify();
    }

    @Test
    public void testRunAllTestsWithConforming() {
        ToStringTester.forInstances(new Conforming())
                      .forSupplier(() -> new Conforming())
                      .verify();
        ToStringTester.forInstances(new Conforming())
                      .withSupplier(() -> new Conforming())
                      .verify();
    }

    @Test
    public void testRunAllTestsWithConformingPublicSupplier() {
        ToStringTester.forClass(ConformingWithPublicConstructor.class)
                      .verify();
        ToStringTester.forSupplier(() -> new ConformingWithPublicConstructor())
                      .verify();
    }

    @Test
    public void empty_to_string_should_throw_illegal_argument_exception() {
        //given
        final class EmptyToStringBean { public String toString() { return ""; }}

        //when
        final Throwable throwable = catchThrowable(
                () -> ToStringTester.forInstances(new EmptyToStringBean()).verify()
                                                  );

        //then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_propagate_instantiation_exception_if_thrown_by_constructor() {
        //given
        final class EmptyToStringBean { public String toString() { return ""; }}

        //when
        final Throwable throwable = catchThrowable(
                () -> ToStringTester.forClass(EmptyToStringBean.class).verify()
                                                  );

        //then
        assertThat(throwable).isInstanceOf(RuntimeException.class)
                             .hasCauseInstanceOf(InstantiationException.class);
    }

    @Test
    public void should_propagate_instantiation_exception_if_thrown_by_constructor2() {
        //given

        //when
        final Throwable throwable = catchThrowable(
                () -> ToStringTester.forClass(IllegalAccessExceptionThrowingBean.class).verify()
                                                  );

        //then
        assertThat(throwable).isInstanceOf(RuntimeException.class)
                             .hasCauseInstanceOf(IllegalAccessException.class);
    }

    //utils

    public static class IllegalAccessExceptionThrowingBean
    {
        IllegalAccessExceptionThrowingBean()
        throws IllegalAccessException
        { throw new IllegalAccessException(); }
        public String toString() { return "perfect"; }
    }

    private static class NonConformingWithNull{
        @Override
        public String toString(){
            return null;
        }
    }

    private static class NonConformingWithDefault{
        @Override
        public String toString(){
            return super.toString();
        }
    }

    private static class NonConformingWithEmpty{
        @Override
        public String toString(){
            return super.toString();
        }
    }

    private static class Conforming{
        @Override
        public String toString(){
            return Randoms.randomAlphanumeric(Randoms.randomInt(1, 10));
        }
    }

    private static class ConformingWithPublicConstructor{
        public ConformingWithPublicConstructor() {}
        @Override
        public String toString(){
            return Randoms.randomAlphanumeric(Randoms.randomInt(1, 10));
        }
    }
}
