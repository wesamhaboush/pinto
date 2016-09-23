package com.codebreeze.testing;

import java.util.*;
import java.util.function.Supplier;

public class ToStringTester<T>
{

    private final Class<T> clazz;
    private final Supplier<T> supplier;
    private final Set<T> ts = new HashSet<>();

    private ToStringTester(
            final Class<T> clazz, final Supplier<T> cutSupplier, final Set<T> ts
                          )
    {
        this.clazz = clazz;
        this.supplier = cutSupplier;
        this.ts.addAll(PintoObjects.firstNonNull(ts, Collections.emptySet()));
    }

    private void testNotNull()
    throws IllegalAccessException, InstantiationException
    {
        for (T t : ts)
        {
            Objects.requireNonNull(t.toString(), "t.toString()");
        }
        if (supplier != null)
        {
            final T t = supplier.get();
            Objects.requireNonNull(t.toString(), "t.toString()");
        }
        if (clazz != null)
        {
            final T t = clazz.newInstance();
            Objects.requireNonNull(t.toString(), "t.toString()");
        }
    }

    private void testNotDefault()
    throws IllegalAccessException, InstantiationException
    {
        for (T t : ts)
        {
            checkDefaultToStringUsed(t);
        }
        if (supplier != null)
        {
            final T t = supplier.get();
            checkDefaultToStringUsed(t);
        }
        if (clazz != null)
        {
            final T t = clazz.newInstance();
            checkDefaultToStringUsed(t);
        }
    }

    private void checkDefaultToStringUsed(T t)
    {
        if (t.toString()
             .equalsIgnoreCase(t.getClass()
                                .getName() + "@" + Integer.toHexString(System.identityHashCode(t))))
        {
            throw new IllegalArgumentException("default toString used, value was " + t.toString());
        }
    }

    private void testNotEmpty()
    throws IllegalAccessException, InstantiationException
    {
        for (T t : ts)
        {
            checkEmptyToString(t);
        }
        if (supplier != null)
        {
            final T t = supplier.get();
            checkEmptyToString(t);
        }
        if (clazz != null)
        {
            final T t = clazz.newInstance();
            checkEmptyToString(t);
        }
    }

    private void checkEmptyToString(T t)
    {
        if (t.toString()
             .trim()
             .isEmpty())
        {
            throw new IllegalArgumentException("t.toString().trim().isEmpty() should be false");
        }
    }

    public void verify()
    {
        try
        {
            testNotNull();
            testNotDefault();
            testNotEmpty();
        }
        catch (final InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <T> Builder<T> forClass(final Class<T> clazz)
    {
        return new Builder<T>().forClass(clazz);
    }

    public static <T> Builder<T> forSupplier(final Supplier<T> cutSupplier)
    {
        return new Builder<T>().forSupplier(cutSupplier);
    }

    public static <T> Builder<T> forInstances(final T... ts)
    {
        return new Builder<T>().forInstances(ts);
    }

    public static class Builder<T>
    {
        private Class<T> clazz;
        private Supplier<T> cutSupplier;
        private Set<T> instances = new HashSet<>();

        private Builder()
        {
        }

        public Builder<T> forClass(final Class<T> clazz)
        {
            this.clazz = clazz;
            return this;
        }

        public Builder<T> forSupplier(final Supplier<T> cutSupplier)
        {
            this.cutSupplier = cutSupplier;
            return this;
        }

        public Builder<T> withSupplier(final Supplier<T> cutSupplier)
        {
            this.cutSupplier = cutSupplier;
            return this;
        }

        public Builder<T> forInstances(T... ts)
        {
            instances.addAll(Arrays.asList(ts));
            return this;
        }

        public void verify()
        {
            new ToStringTester<T>(clazz, cutSupplier, instances).verify();
        }
    }
}
