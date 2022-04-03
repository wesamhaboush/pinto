package com.codebreeze.testing;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

import static com.codebreeze.testing.Randoms.randomInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class EqualAndHashcodeTestTest
{

    @Test(expected=AssertionError.class)
    public void testRunAllTestsWhenDoesntWork() {
        final Supplier<NonConforming> nonConformingFactory = new Supplier<NonConforming>(){
            final int field1 = randomInt();
            final int field2 = randomInt();
            @Override
            public NonConforming get() {
                return new NonConforming(field1, field2);
            }

        };
        EqualAndHashcodeTest
                .forClass(NonConforming.class, nonConformingFactory)
                .verify();
    }

    @Test
    public void testRunAllTestsWorks() {
        final Supplier<Conforming> conformingFactory = new Supplier<Conforming>(){
            final int field1 = randomInt();
            final int field2 = randomInt();
            @Override
            public Conforming get() {
                return new Conforming(field1, field2);
            }

        };
        EqualAndHashcodeTest
                .forClass(Conforming.class, conformingFactory)
                .verify();
    }

    @Test
    public void testRunAllTestsWorksWithEnums() {
        final Supplier<ClassWithEnumsAndNonFinal> classWithEnumsFactory = () -> new ClassWithEnumsAndNonFinal(Dummy.values()[0], new ArrayList<>());
        EqualAndHashcodeTest
                .forClass(ClassWithEnumsAndNonFinal.class, classWithEnumsFactory)
                .verify();
        EqualAndHashcodeTest
                .forClass(ClassWithEnumsAndNonFinal.class)
                .withSupplier(classWithEnumsFactory)
                .verify();
    }

    @Test
    public void verify_should_ignore_jacoco_field() {
        EqualAndHashcodeTest
                .forClass(ClassWithJacoco.class)
                .verify();
    }

    @Test(expected=RuntimeException.class)
    public void testRunAllTestsDoesNotWorkWithFinalClassesThatHasNoSuppliedFactories() {
        final Supplier<ClassWithFinal> classWithFinalFactory = new Supplier<ClassWithFinal>(){
            private final FinalClass fc = new FinalClass();
            @Override
            public ClassWithFinal get() {
                return new ClassWithFinal(fc);
            }

        };
        EqualAndHashcodeTest
                .forClass(ClassWithFinal.class, classWithFinalFactory)
                .verify();
    }

    @Test
    public void testRunAllTestsWorksWithFinalClassesThatHasSuppliedFactories() {

        final Class<FinalClass> aClass = FinalClass.class;
        final Supplier<FinalClass> factory = () -> new FinalClass();

        final Supplier<ClassWithFinal> classWithFinalFactory = new Supplier<ClassWithFinal>(){
            private final FinalClass fc = new FinalClass();
            @Override
            public ClassWithFinal get() {
                return new ClassWithFinal(fc);
            }
        };
        EqualAndHashcodeTest
                .forClass(ClassWithFinal.class, classWithFinalFactory)
                .withComplexTypeSupplier(aClass, factory)
                .verify();
    }

    @Test
    public void does_not_allow_same_instance_to_be_returned_by_cut_supplier() {
        //given
        final class SomeBean {};
        final SomeBean someBean = new SomeBean();
        final Supplier<SomeBean> someBeanSupplier = () -> someBean;
        //when
        final Throwable thrown = catchThrowable(
                () -> EqualAndHashcodeTest.forClass(SomeBean.class, someBeanSupplier).verify()
                                               );

        //then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void does_not_allow_non_equal_instance_to_be_returned_by_cut_supplier() {
        //given
        final class SomeBean { @Override public boolean equals(Object o) { return false; }};
        final Supplier<SomeBean> someBeanSupplier = () -> new SomeBean();

        //when
        final Throwable thrown = catchThrowable(
                () -> EqualAndHashcodeTest.forClass(SomeBean.class, someBeanSupplier).verify()
                                               );

        //then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void excluded_fields_are_excluded()
    {
        //given
        class SomeBean {
            private String name;
            private int age;

            public SomeBean(final String name)
            {
                this.name = name;
            }

            @Override
            public boolean equals(final Object o)
            {
                if(o == null) return false;
                final SomeBean that = (SomeBean)o;
                return Objects.equals(this.name, that.name);
            }

            @Override
            public int hashCode()
            {
                return Objects.hash(this.name);
            }
        }
        final Supplier<SomeBean> someBeanSupplier = () -> new SomeBean("A");

        //when
        final Throwable thrown = catchThrowable(
                () -> EqualAndHashcodeTest.forClass(SomeBean.class, someBeanSupplier)
                                          .excludeFields("age")
                                          .verify()
                                               );

        //then
        assertThat(thrown)
                .isNull();
    }

    @Test
    public void included_fields_are_exclusively_included()
    {
        //given
        class SomeBean {
            private String name;
            private int age;

            public SomeBean(final String name)
            {
                this.name = name;
            }

            @Override
            public boolean equals(final Object o)
            {
                if(o == null) return false;
                final SomeBean that = (SomeBean)o;
                return Objects.equals(this.name, that.name);
            }

            @Override
            public int hashCode()
            {
                return Objects.hash(this.name);
            }
        }
        final Supplier<SomeBean> someBeanSupplier = () -> new SomeBean("A");

        //when
        final Throwable thrown = catchThrowable(
                () -> EqualAndHashcodeTest.forClass(SomeBean.class, someBeanSupplier)
                                          .includeFields("name")
                                          .verify()
                                               );

        //then
        assertThat(thrown)
                .isNull();
    }

    @Test
    public void illegal_access_exceptions_are_propagated()
    {
        //given
        class SomeBean {
            private String name;
        //    private int age;

            public SomeBean(final String name)
            {
                this.name = name;
            }

            @Override
            public boolean equals(final Object o)
            {
                if(o == null) return false;
                final SomeBean that = (SomeBean)o;
                return Objects.equals(this.name, that.name);
            }

            @Override
            public int hashCode()
            {
                return Objects.hash(this.name);
            }
        }
        final Supplier<SomeBean> someBeanSupplier = () -> new SomeBean("A");

        //when
        final Throwable thrown = catchThrowable(
                () -> EqualAndHashcodeTest.forClass(SomeBean.class, someBeanSupplier)
                                          .includeFields("name")
                                          .verify()
                                               );

        //then
        assertThat(thrown)
                .isNull();
    }

    @Test
    public void exception_propagated_for_beans_without_zero_argument_constructors()
    {
        //given
        class SomeBean {
            public SomeBean(final String name)
            {
            }
        }

        //when
        final Throwable thrown = catchThrowable(() -> EqualAndHashcodeTest.forClass(SomeBean.class).verify());

        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void cannot_use_both_include_and_exclude_lists()
    {
        //given
        class SomeBean {
            private String name;
            private String age;
        }

        //when
        final Throwable thrown = catchThrowable(() -> EqualAndHashcodeTest
                                                        .forClass(SomeBean.class)
                                                        .includeFields("name")
                                                        .excludeFields("age")
                                                        .verify());

        //then
        assertThat(thrown).isInstanceOf(IllegalStateException.class);
    }


    //utils

    private static class NonConforming{
        private final int field1;
        private final int field2;

        private NonConforming(final int field1, final int field2){
            this.field1 = field1;
            this.field2 = field2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) { return false; }
            if (obj == this) { return true; }
            if (obj.getClass() != getClass()) {
                return false;
            }
            final NonConforming other = (NonConforming) obj;
            return Objects.equals(this.field1, other.field1);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 41 * hash + this.field1;
            hash = 41 * hash + this.field2;
            return hash;
        }
    }

    private static class Conforming{
        private final int field1;
        private final int field2;

        private Conforming(final int field1, final int field2){
            this.field1 = field1;
            this.field2 = field2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) { return false; }
            if (obj == this) { return true; }
            if (obj.getClass() != getClass()) {
                return false;
            }
            final Conforming other = (Conforming) obj;
            return Objects.equals(this.field1, other.field1)
                    && Objects.equals(this.field2, other.field2);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 41 * hash + this.field1;
            hash = 41 * hash + this.field2;
            return hash;
        }
    }

    private enum Dummy { A, B, C}
    private static class ClassWithEnumsAndNonFinal{
        private final Dummy d;
        private final Collection<String> col = new ArrayList<>();

        public ClassWithEnumsAndNonFinal(final Dummy d, final Collection<String> col){
            this.d = d;
            this.col.addAll(col);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) { return false; }
            if (obj == this) { return true; }
            if (obj.getClass() != getClass()) {
                return false;
            }
            final ClassWithEnumsAndNonFinal other = (ClassWithEnumsAndNonFinal) obj;
            return Objects.equals(this.d, other.d)
                    && Objects.equals(this.col, other.col);
        }

        @Override
        public int hashCode() {
            return Objects.hash(d, col);
        }
    }

    private static class ClassWithFinal{
        private final FinalClass f;

        public ClassWithFinal(final FinalClass f){
            this.f = f;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ClassWithFinal other = (ClassWithFinal) obj;
            return Objects.equals(this.f, other.f);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(f);
        }
    }

    private static final class FinalClass{

    }

    private static class ClassWithJacoco {
        private String $jacocoData;
        private String x;

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            ClassWithJacoco that = (ClassWithJacoco) o;
            return Objects.equals(x, that.x);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(x);
        }
    }
}
