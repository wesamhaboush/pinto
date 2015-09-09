package com.codebreeze.testing;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class RandomizedInstanceTest {
    @Test
    public void testVanillaBeanWorks() {
        final TestBean testBean = RandomizedInstance.forClass(TestBean.class).get();

        assertThat(testBean, notNullValue());
        assertThat(testBean.name, notNullValue());
        assertThat(testBean.age, not(equalTo(Integer.MIN_VALUE)));
        assertThat(testBean.salary, not(equalTo(Long.MIN_VALUE)));
        assertThat(testBean.secretCode, not(equalTo(Byte.MIN_VALUE)));
        assertThat(testBean.testBean2, notNullValue());
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