package com.codebreeze.testing;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.function.Supplier;

import static com.codebreeze.testing.Randoms.randomInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


public class GetterAndSetterTestTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testRunWithGoodBean() throws Exception {
        GetterAndSetterTest
                .forClass(GoodBean.class)
                .verify();

        GetterAndSetterTest
                .forClass(GoodBean.class, () -> new GoodBean())
                .verify();
    }

    @Test
    public void excluded_fields_should_be_excluded() throws Exception {
        final class TestBean {
            public String getEx1()
            {
                return ex2; //this is wrong, but we do not care cz we are excluding it
            }

            public String getEx2()
            {
                return ex2;
            }

            final String ex1;
            final String ex2;

            TestBean(final String ex1, final String ex2)
            {
                this.ex1 = ex1;
                this.ex2 = ex2;
            }
        }
        GetterAndSetterTest
                .forClass(TestBean.class)
                .withSupplier(() -> new TestBean("a", "b"))
                .excludeFields("ex1")
                .verify();
    }

    @Test
    public void included_fields_should_be_included_and_nothing_else() throws Exception {
        final class TestBean {
            public String getEx1()
            {
                return ex2; //this is wrong, but we do not care cz we are excluding it
            }

            public String getEx2()
            {
                return ex2;
            }

            public boolean isEx3() { return ex3; }

            final String ex1;
            final String ex2;
            final boolean ex3;

            TestBean(final String ex1, final String ex2, final boolean ex3)
            {
                this.ex1 = ex1;
                this.ex2 = ex2;
                this.ex3 = ex3;
            }
        }
        GetterAndSetterTest
                .forClass(TestBean.class)
                .withSupplier(() -> new TestBean("a", "b", true))
                .includeFields("ex2", "ex3")
                .verify();
    }

    @Test
    public void should_reject_beans_with_missing_setters_in_strict_mode() throws Exception {
        final class TestBean {
            public String getEx1()
            {
                return ex1;
            }

            final String ex1;

            TestBean(final String ex1)
            {
                this.ex1 = ex1;
            }
        }
        //when
        final Throwable throwable = catchThrowable(() -> GetterAndSetterTest.forClass(TestBean.class)
                                                                            .withSupplier(() -> new TestBean("a"))
                                                                            .strict(true)
                                                                            .verify());

        //then
        assertThat(throwable).isInstanceOf(AssertionError.class)
                .hasMessageContaining("and field [ex1] did not");
    }

    @Test
    public void should_reject_using_include_and_exclude_at_the_same_time() throws Exception {
        final class TestBean {
            public String getEx1()
            {
                return ex1;
            }

            final String ex1;

            TestBean(final String ex1)
            {
                this.ex1 = ex1;
            }
        }
        //when
        final Throwable throwable = catchThrowable(() -> GetterAndSetterTest.forClass(TestBean.class)
                                                                            .withSupplier(() -> new TestBean("a"))
                                                                            .includeFields("ex1")
                                                                            .excludeFields("ex1")
                                                                            .verify());

        //then
        assertThat(throwable).isInstanceOf(IllegalStateException.class)
                             .hasMessageContaining("cannot have both include and exclude fields non-empty");
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

    @Test(expected=NullPointerException.class)
    public void should_throw_npe_when_missing_factory_for_complex_final_class() throws Exception {
        final class FinalClass
        {
        }
        class Bean
        {
            private FinalClass finalClassField;
            public Bean(){}
            public FinalClass getFinalClassField()
            {
                return finalClassField;
            }
        }
        GetterAndSetterTest
                .forClass(Bean.class, () -> new Bean())
                .verify();
    }

    @Test(expected=IllegalArgumentException.class)
    public void should_throw_illegal_argument_when_missing_no_args_constructor() throws Exception {
        class Bean
        {
        }
        GetterAndSetterTest
                .forClass(Bean.class)
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
        private Short c;
        private Short[] d;
        private short[] e;
        private Long[] f;
        private long[] g;
        private Double h;
        private double[] i;
        private Double[] j;
        private Float k;
        private float[] l;
        private Float[] m;
        private boolean[] n;
        private Boolean[] o;
        private char[] p;
        private Character[] q;
        private Character r;
        private Byte s;
        private String t;

        private GoodBean()
        {
        }

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

        public Short getC()
        {
            return c;
        }

        public void setC(final Short c)
        {
            this.c = c;
        }

        public Short[] getD()
        {
            return d;
        }

        public void setD(final Short[] d)
        {
            this.d = d;
        }

        public short[] getE()
        {
            return e;
        }

        public void setE(final short[] e)
        {
            this.e = e;
        }

        public Long[] getF()
        {
            return f;
        }

        public void setF(final Long[] f)
        {
            this.f = f;
        }

        public long[] getG()
        {
            return g;
        }

        public void setG(final long[] g)
        {
            this.g = g;
        }

        public Double getH()
        {
            return h;
        }

        public void setH(final Double h)
        {
            this.h = h;
        }

        public double[] getI()
        {
            return i;
        }

        public void setI(final double[] i)
        {
            this.i = i;
        }

        public Double[] getJ()
        {
            return j;
        }

        public void setJ(final Double[] j)
        {
            this.j = j;
        }

        public Float getK()
        {
            return k;
        }

        public void setK(final Float k)
        {
            this.k = k;
        }

        public float[] getL()
        {
            return l;
        }

        public void setL(final float[] l)
        {
            this.l = l;
        }

        public Float[] getM()
        {
            return m;
        }

        public void setM(final Float[] m)
        {
            this.m = m;
        }

        public boolean[] getN()
        {
            return n;
        }

        public void setN(final boolean[] n)
        {
            this.n = n;
        }

        public Boolean[] getO()
        {
            return o;
        }

        public void setO(final Boolean[] o)
        {
            this.o = o;
        }

        public Character[] getQ()
        {
            return q;
        }

        public void setQ(final Character[] q)
        {
            this.q = q;
        }

        public char[] getP()
        {
            return p;
        }

        public void setP(final char[] p)
        {
            this.p = p;
        }

        public Character getR()
        {
            return r;
        }

        public void setR(final Character r)
        {
            this.r = r;
        }

        public Byte getS()
        {
            return s;
        }

        public void setS(final Byte s)
        {
            this.s = s;
        }

        public String getT()
        {
            return t;
        }

        public void setT(final String t)
        {
            this.t = t;
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
