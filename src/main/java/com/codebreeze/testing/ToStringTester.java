package com.codebreeze.testing;

import com.google.common.base.Throwables;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.function.Supplier;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

public class ToStringTester<T> {

    private final Class<T> clazz;
    private final Supplier<T> supplier;
    private final Set<T> ts = new HashSet<T>();

    private ToStringTester(final Class<T> clazz, Supplier<T> cutSupplier, Set<T> ts) {
        this.clazz = clazz;
        this.supplier = cutSupplier;
        if (!isEmpty(ts)) {
            this.ts.addAll(ts);
        }
    }

    private void testNotNull() {
        for (T t : ts) {
            Validate.notNull(t.toString(), "t.toString()");
        }
        if(supplier != null){
            final T t = supplier.get();
            Validate.notNull(t.toString(), "t.toString()");
        }
        if(clazz != null){
            try {
                final T t = clazz.newInstance();
                Validate.notNull(t.toString(), "t.toString()");
            } catch (InstantiationException | IllegalAccessException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    private void testNotDefault() {
        for (T t : ts) {
            Validate.isTrue(!t.toString().equalsIgnoreCase(
                    t.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(t))
            ), "default toString used", "value was " + t.toString());
        }
        if(supplier != null){
            final T t = supplier.get();
            Validate.isTrue(!t.toString().equalsIgnoreCase(
                    t.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(t))
            ), "default toString used", "value was " + t.toString());
        }
        if(clazz != null){
            try {
                final T t = clazz.newInstance();
                Validate.isTrue(!t.toString().equalsIgnoreCase(
                        t.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(t))
                ), "default toString used", "value was " + t.toString());
            } catch (InstantiationException | IllegalAccessException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    private void testNotEmpty() {
        for (T t : ts) {
            Validate.isTrue(!t.toString().trim().isEmpty(), "t.toString().trim().isEmpty()", "should be false");
        }
        if(supplier != null){
            final T t = supplier.get();
            Validate.isTrue(!t.toString().trim().isEmpty(), "t.toString().trim().isEmpty()", "should be false");
        }
        if(clazz != null){
            try {
                final T t = clazz.newInstance();
                Validate.isTrue(!t.toString().trim().isEmpty(), "t.toString().trim().isEmpty()", "should be false");
            } catch (InstantiationException | IllegalAccessException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    public void verify() {
        testNotNull();
        testNotDefault();
        testNotEmpty();
    }

    public static <T> Builder<T> forClass(final Class<T> clazz){
        return new Builder<T>().forClass(clazz);
    }

    public static <T> Builder<T> forSupplier(final Supplier<T> cutSupplier){
        return new Builder<T>().forSupplier(cutSupplier);
    }

    public static <T> Builder<T> forInstances(final T... ts){
        return new Builder<T>().forInstances(ts);
    }

    public static class Builder<T> {
        private Class<T> clazz;
        private Supplier<T> cutSupplier;
        private Set<T> instances = new HashSet<>();

        private Builder() {
        }

        public Builder<T> forClass(final Class<T> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Builder<T> forSupplier(final Supplier<T> cutSupplier) {
            this.cutSupplier = cutSupplier;
            return this;
        }

        public Builder<T> withSupplier(final Supplier<T> cutSupplier) {
            this.cutSupplier = cutSupplier;
            return this;
        }

        public Builder<T> forInstances(T...ts) {
            instances.addAll(Arrays.asList(ts));
            return this;
        }

        public void verify() {
            new ToStringTester<T>(clazz, cutSupplier, instances).verify();
        }
    }
}
