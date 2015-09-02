package com.codebreeze.testing;

import org.junit.Test;

public class ToStringTesterTest {

    @Test(expected=RuntimeException.class)
    public void testRunAllTestsWithNonConformingNull() {
        new ToStringTester<>(new NonConformingWithNull()).runAllTests();
    }

    @Test(expected=RuntimeException.class)
    public void testRunAllTestsWithNonConformingDefault() {
        new ToStringTester<>(new NonConformingWithDefault()).runAllTests();
    }

    @Test(expected=RuntimeException.class)
    public void testRunAllTestsWithNonConformingEmpty() {
        new ToStringTester<>(new NonConformingWithEmpty()).runAllTests();
    }

    @Test
    public void testRunAllTestsWithConforming() {
        new ToStringTester<>(new Conforming()).runAllTests();
    }

    //utils

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
}
