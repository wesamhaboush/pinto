package com.codebreeze.testing;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import static com.codebreeze.testing.Randoms.randomInt;

public class EqualHashcodeTesterTest {

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
            return new EqualsBuilder()
                    .append(this.field1, other.field1)
                    .isEquals();
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
            return new EqualsBuilder()
                    .append(this.field1, other.field1)
                    .append(this.field2, other.field2)
                    .isEquals();
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
            return new EqualsBuilder()
                    .append(this.d, other.d)
                    .append(this.col, other.col)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().
                    append(d).
                    append(col).
                    toHashCode();
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
            return new EqualsBuilder()
                    .append(this.f, other.f)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().
                    append(f).
                    toHashCode();
        }
    }

    private static final class FinalClass{

    }
}
