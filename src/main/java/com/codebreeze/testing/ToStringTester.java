package com.codebreeze.testing;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang3.ArrayUtils.isEmpty;

public class ToStringTester<T> {

    private final List<T> ts = new ArrayList<T>();

    public ToStringTester(final T... ts) {
        Validate.noNullElements(ts, "cannot test a null object for toString implementation!");

        if (!isEmpty(ts)) {
            this.ts.addAll(Arrays.asList(ts));
        }
    }

    private void testNotNull() {
        for (T t : ts) {
            Validate.notNull(t.toString(), "t.toString()");
        }
    }

    private void testNotDefault() {
        for (T t : ts) {
            Validate.isTrue(!t.toString().equalsIgnoreCase(
                    t.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(t))
            ), "default toString used", "value was " + t.toString());
        }
    }

    private void testNotEmpty() {
        for (T t : ts) {
            Validate.isTrue(!t.toString().trim().isEmpty(), "t.toString().trim().isEmpty()", "should be false");
        }
    }

    public void runAllTests() {
        testNotNull();
        testNotDefault();
        testNotEmpty();
    }
}
