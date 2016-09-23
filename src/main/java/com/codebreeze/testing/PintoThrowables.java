package com.codebreeze.testing;


import java.util.concurrent.Callable;

import static com.codebreeze.testing.PintoObjects.checkNotNull;

public class PintoThrowables
{
    private PintoThrowables() {
        throw new UnsupportedOperationException("cannot instantiate " + PintoThrowables.class.getSimpleName());
    }

    public static RuntimeException propagate(final Throwable throwable) {
        propagateIfPossible(checkNotNull(throwable));
        return new RuntimeException(throwable);
    }

    public static void propagateIfPossible(final Throwable throwable) {
        propagateIfInstanceOf(throwable, Error.class);
        propagateIfInstanceOf(throwable, RuntimeException.class);
    }

    public static <X extends Throwable> void propagateIfInstanceOf(
            final Throwable throwable,
            final Class<X> declaredType) throws X {
        // Check for null is needed to avoid frequent JNI calls to isInstance().
        if (throwable != null && declaredType.isInstance(throwable)) {
            throw declaredType.cast(throwable);
        }
    }

    public static <T> T propagate(final Callable<T> callable)
    {
        try
        {
            return callable.call();
        }
        catch (final Throwable r)
        {
            throw propagate(r);
        }
    }
}
