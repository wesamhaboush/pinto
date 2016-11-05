package com.codebreeze.testing;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class Verify
{
    private Verify()
    {
        throw new UnsupportedOperationException(Verify.class.getSimpleName() + " cannot be instantiated by design");
    }

    public static <T> void uninstantiable(final Class<T> tClass)
    {
        if (tClass == null)
        {
            throw new NullPointerException("cannot test null classes for uninstantiability");
        }
        //given
        //note: the reason we have to filter and not just take the first is because
        //some instrumentations add more constructors.
        final Constructor<?> noArgsConstructor = Arrays.asList(tClass.getDeclaredConstructors())
                                                       .stream()
                                                       .filter(c -> c.getParameterCount() == 0)
                                                       .findFirst()
                                                       .orElseThrow(() -> new IllegalArgumentException("cannot find 0 argument constructor for class: " + tClass));
        noArgsConstructor.setAccessible(true);

        //when
        final Throwable throwable = catchThrowable(() -> noArgsConstructor.newInstance());

        //then
        assertThat(throwable, anyOf(instanceOf(InvocationTargetException.class),
                                    instanceOf(UnsupportedOperationException.class)));
        assertThat(throwable.getCause(), anyOf(instanceOf(UnsupportedOperationException.class), nullValue()));
    }

    public static <T> void uninstantiable(final Supplier<T> tClassSupplier)
    {
        if (tClassSupplier == null)
        {
            throw new NullPointerException("cannot test with null supplier for uninstantiability");
        }
        //when
        final Throwable throwable = catchThrowable(() -> tClassSupplier.get());

        //then
        assertThat(throwable, anyOf(instanceOf(InvocationTargetException.class),
                                    instanceOf(UnsupportedOperationException.class)));
        assertThat(throwable.getCause(), anyOf(instanceOf(UnsupportedOperationException.class), nullValue()));
    }

    public static <T extends Enum<T>> void useful(Class<T> enumClass)
    {
        if(enumClass == null)
        {
            throw new NullPointerException("cannot test with a null enum");
        }
        assertTrue("enums must have some members to be useful, this one has none", enumClass.getEnumConstants().length > 0);
        assertEquals(Arrays.stream(enumClass.getEnumConstants())
                           .map(item -> item.name())
                           .map(s -> T.valueOf(enumClass, s))
                           .toArray().length, enumClass.getEnumConstants().length);
    }

    private static <T> Throwable catchThrowable(final Callable<T> c)
    {
        try
        {
            c.call();
            return null;
        }
        catch (final Throwable throwable)
        {
            return throwable;
        }
    }
}
