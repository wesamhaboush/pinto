package com.codebreeze.testing;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.function.Supplier;

import static com.codebreeze.testing.Randoms.randomInt;


public class GetterAndSetterTestTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testRunWithGoodBean() throws Exception {
        GetterAndSetterTest
                .forClass(GoodBean.class)
                .verify();
    }

    @Test
    public void testRunWithGoodBeanWithSupplier() throws Exception {
        GetterAndSetterTest
                .forClass(GoodBean.class)
                .withSupplier(GoodBean::new)
                .verify();
    }

    @Test(expected = AssertionError.class)
    public void testRunWithBadBean() throws Exception {
        GetterAndSetterTest
                .forClass(BadBean.class)
                .withSupplier(BadBean::new)
                .verify();
    }

    @Test
    public void testRunWithNoSettersBean() throws Exception {
        GetterAndSetterTest
                .forClass(NoSettersBean.class)
                .withSupplier(NoSettersBean::new)
                .verify();
    }

    @Test
    public void testRunWithNoGettersBean() throws Exception {
        GetterAndSetterTest
                .forClass(NoGettersBean.class)
                .withSupplier(NoGettersBean::new)
                .verify();
    }

    @Test
    public void testRunWithComplexObjectsBean() throws Exception {
        final Supplier<int[]> intArrayFactory = () -> new int[]{randomInt()};
        GetterAndSetterTest
                .forClass(ComplexObjectsBean.class)
                .withSupplier(ComplexObjectsBean::new)
                .withComplexTypeSupplier(int[].class, intArrayFactory)
                .verify();
    }

    //utils
    private static class GoodBean {
        private int a;
        private long b;

        public long getB() {
            return b;
        }

        public void setB(long b) {
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }

    private static class BadBean {
        private int a;
        private int b;

        public int getB() {
            //here it is misbehaving, this is why it is a bad bean
            return a;
        }

        public void setB(int b) {
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }

    private static class NoSettersBean {
        private int a;
        private int b;

        public int getB() {
            return b;
        }

        public int getA() {
            return a;
        }
    }

    private static class NoGettersBean {
        private int a;
        private long b;

        public void setB(long b) {
            this.b = b;
        }

        public void setA(int a) {
            this.a = a;
        }
    }

    private static class ComplexObjectsBean {
        private GoodBean a;
        private long b;
        private List<String> c;
        private int[] d;
        private byte[] bs;
        private String[] ss;

        public int[] getD() {
            return d;
        }

        public void setD(int[] d) {
            this.d = d;
        }

        public List<String> getC() {
            return c;
        }

        public void setC(List<String> c) {
            this.c = c;
        }

        public long getB() {
            return b;
        }

        public void setB(long b) {
            this.b = b;
        }

        public GoodBean getA() {
            return a;
        }

        public void setA(GoodBean a) {
            this.a = a;
        }

        public byte[] getBs() {
            return bs;
        }

        public void setBs(final byte[] bs) {
            this.bs = bs;
        }

        public String[] getSs() {
            return ss;
        }

        public void setSs(final String[] ss) {
            this.ss = ss;
        }
    }

}
