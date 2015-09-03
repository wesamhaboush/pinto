package com.codebreeze.testing;

import org.junit.Test;

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
        ToStringTester.forInstances(new Conforming()).verify();
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
