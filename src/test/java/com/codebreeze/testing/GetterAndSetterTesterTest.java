package com.codebreeze.testing;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.codebreeze.testing.Randoms.randomInt;


public class GetterAndSetterTesterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testRunWithGoodBean() throws Exception {
        final Supplier<GoodBean> goodBeanFactory = () -> new GoodBean();

        new GetterAndSetterTester<>(goodBeanFactory).run();
    }

    @Test(expected = AssertionError.class)
    public void testRunWithBadBean() throws Exception {
        final Supplier<BadBean> badBeanFactory = () -> new BadBean();

        new GetterAndSetterTester<>(badBeanFactory).run();
    }

    @Test
    public void testRunWithNoSettersBean() throws Exception {
        final Supplier<NoSettersBean> noSettersBeanFactory = () -> new NoSettersBean();

        new GetterAndSetterTester<>(noSettersBeanFactory).run();
    }

    @Test
    public void testRunWithNoGettersBean() throws Exception {
        final Supplier<NoGettersBean> noGettersBeanFactory = () -> new NoGettersBean();

        new GetterAndSetterTester<>(noGettersBeanFactory).run();
    }

    @Test
    public void testRunWithComplexObjectsBean() throws Exception {
        final Supplier<ComplexObjectsBean> complexObjectsBeanFactory = () -> new ComplexObjectsBean();
        final Supplier<int[]> intArrayFactory = () -> new int[]{randomInt()};
        Map<Class<?>, Supplier<?>> nonStandardFactories = new HashMap<Class<?>, Supplier<?>>() {
            {
                put(int[].class, intArrayFactory);
            }
        };
        new GetterAndSetterTester<>(complexObjectsBeanFactory,
                Collections.<String>emptyList(), nonStandardFactories).run();
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
