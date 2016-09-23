package com.codebreeze.testing;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class RandomizedInstanceTest {
    @Test
    public void testVanillaBeanWorks() {
        final TestBean testBean = RandomizedInstance
                .forClass(TestBean.class)
                .get();

        assertThat(testBean).isNotNull();
        assertThat(testBean.name).isNotNull();
        assertThat(testBean.age).isNotEqualTo(Integer.MIN_VALUE);
        assertThat(testBean.salary).isNotEqualTo(Long.MIN_VALUE);
        assertThat(testBean.secretCode).isNotEqualTo(Byte.MIN_VALUE);
        assertThat(testBean.testBean2).isNotNull();
    }

    @Test
    public void throws_exception_if_supplier_always_returns_the_same_instance() {
        //given
        final class TestBean2 {
            private final int age;

            private TestBean2(int age)
            {
                this.age = age;
            }
        }
        final TestBean2 testBean2 = new TestBean2(1);

        //when
        final Throwable throwable = catchThrowable(() -> RandomizedInstance
                .forClass(TestBean2.class, () -> testBean2)
                .get());

        //then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void included_fields_are_randomized_exclusively() {
        //given
        final class TestBean2 {
            private final int age;
            private final String name;

            private TestBean2(int age, final String name)
            {
                this.age = age;
                this.name = name;
            }
        }

        //when
        final TestBean2 testBean2 = RandomizedInstance.forClass(TestBean2.class, () -> new TestBean2(1, ""))
                                                      .includeFields("age")
                                                      .get();

        //then
        assertThat(testBean2.name).isEmpty();
    }

    @Test
    public void throws_illegal_argument_exception_if_bean_does_not_have_no_arg_constructor() {
        //given
        final class TestBean2 {
            private final int age;

            private TestBean2(int age)
            {
                this.age = age;
            }
        }

        //when
        final Throwable throwable = catchThrowable(() -> RandomizedInstance
                .forClass(TestBean2.class)
                .get());

        //then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void throws_illegal_argument_exception_if_both_exclusion_and_inclusion_lists_used() {
        //given
        final class TestBean2 {
            private final int age;

            private TestBean2(int age)
            {
                this.age = age;
            }
        }

        //when
        final Throwable throwable = catchThrowable(() -> RandomizedInstance
                .forClass(TestBean2.class, () -> new TestBean2(1))
                .includeFields("age")
                .excludeFields("age")
                .get());

        //then
        assertThat(throwable).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void excluded_fields_are_not_randomized() {
        //given
        final class TestBean2 {
            private final int age;
            private final String name;

            private TestBean2(int age, final String name)
            {
                this.age = age;
                this.name = name;
            }
        }

        //when
        final TestBean2 testBean2 = RandomizedInstance.forClass(TestBean2.class)
                                                      .withSupplier(() -> new TestBean2(1, ""))
                                                      .excludeFields("name")
                                                      .get();

        //then
        assertThat(testBean2.name).isEmpty();
    }

    @Test
    public void randomizes_enums() {
        //given
        final class TestBean2 {
            private final TimeUnit timeUnit;

            private TestBean2(final TimeUnit timeUnit)
            {
                this.timeUnit = timeUnit;
            }
        }

        //when
        final TestBean2 testBean2 = RandomizedInstance.forClass(TestBean2.class, () -> new TestBean2(null))
                                                      .get();

        //then
        assertThat(testBean2.timeUnit).isNotNull();
    }

    @Test
    public void randomizes_complex_types_using_our_supplier() {
        //given
        final class TestBean2 {
            private final String name;

            TestBean2(final String name)
            {
                this.name = name;
            }
        }

        final class TestBean3
        {
            private final TestBean2 testBean2;

            TestBean3(final TestBean2 testBean2)
            {
                this.testBean2 = testBean2;
            }
        }

        //when
        final TestBean3 testBean3 = RandomizedInstance.forClass(TestBean3.class, () -> new TestBean3(null))
                                                      .withComplexTypeSupplier(
                                                              TestBean2.class, () -> new TestBean2("mango")
                                                                              )
                                                      .get();

        //then
        assertThat(testBean3.testBean2.name).isEqualTo("mango");
    }


    private static class TestBean {
        private final String name;
        private final int age;
        private final long salary;
        private final byte secretCode;
        private final TestBean2 testBean2;

        private TestBean() {
            name = null;
            age = Integer.MIN_VALUE;
            salary = Long.MIN_VALUE;
            this.secretCode = Byte.MIN_VALUE;
            this.testBean2 = null;
        }


        private TestBean(final String name,
                         final int age,
                         final long salary,
                         final byte secretCode,
                         final TestBean2 testBean2) {
            this.name = name;
            this.age = age;
            this.salary = salary;
            this.secretCode = secretCode;
            this.testBean2 = testBean2;
        }
    }

    private static class TestBean2 {}
}
