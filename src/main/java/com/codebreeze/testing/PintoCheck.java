package com.codebreeze.testing;

import java.util.Objects;
import java.util.function.Supplier;

public class PintoCheck
{
    private PintoCheck()
    {
        throw new UnsupportedOperationException("uninstantiable class: "
                                                + PintoCheck.class.getSimpleName());
    }

    public static class State
    {
        private State()
        {
            throw new UnsupportedOperationException("uninstantiable class: "
                                                    + State.class.getSimpleName());
        }

        public static <T> void notSame(final T t1, final T t2, final Supplier<String> msgSupplier)
        {
            notSame(t1, t2, msgSupplier.get());
        }

        public static <T> void notSame(final T t1, final T t2, final String msg)
        {
            if(Objects.equals(t1, t2))
            {
                throw new IllegalStateException(msg);
            }
        }
    }

    public static class Argument
    {
        private Argument()
        {
            throw new UnsupportedOperationException("uninstantiable class: "
                                                    + Argument.class.getSimpleName());
        }

        public static void notLessThan(final int value, final int min, String s)
        {
            if (value < min)
            {
                throw new IllegalArgumentException(s);
            }
        }

        public static void notLessThan(final int value, final int min, final Supplier<String> msgSupplier)
        {
            notLessThan(value, min, msgSupplier.get());
        }
    }
}
