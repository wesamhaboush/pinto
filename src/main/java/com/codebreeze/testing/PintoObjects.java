package com.codebreeze.testing;

public class PintoObjects
{
    private PintoObjects()
    {
        throw new UnsupportedOperationException("uninstantiable class: " + PintoObjects.class.getSimpleName());
    }

    public static <T> T firstNonNull(final T t1, final T t2)
    {
        return t1 == null ? t2 : t1;
    }

    public static <T> T checkNotNull(final T t)
    {
        if (t == null)
        {
            throw new NullPointerException();
        }
        return t;
    }
}
