package com.codebreeze.testing;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static com.codebreeze.testing.PintoObjects.firstNonNull;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

class PintoCollections
{

    private PintoCollections()
    {
        throw new UnsupportedOperationException("uninstantiable class: " + this.getClass().getSimpleName());
    }

    @SafeVarargs
    static <T> HashSet<T> hashSet(final T... ts)
    {
        return new HashSet<T>()
        {{
            addAll(asList(requireNonNull(ts)));
        }};
    }

    static <T> boolean isEmpty(final Collection<T> collection)
    {
        return firstNonNull(collection, Collections.emptyList()).isEmpty();
    }

    static <E> Set<Set<E>> powerSet(final Set<E> set)
    {
        return Collections.unmodifiableSet(new PowerSet<>(requireNonNull(set)));
    }

    private static final class PowerSet<E>
            extends AbstractSet<Set<E>>
    {
        final Map<E, Integer> inputSet;

        PowerSet(final Set<E> input)
        {
            if (input.size() > 30)
            {
                throw new IllegalArgumentException(
                        String.format("Too many elements to create power set: %s > 30", input.size()));
            }
            this.inputSet = PintoCollections.indexMap(input);
        }

        @Override
        public int size()
        {
            return 1 << inputSet.size();
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public Iterator<Set<E>> iterator()
        {
            return new AbstractIndexedIterator<Set<E>>(size())
            {
                @Override
                protected Set<E> get(final int setBits)
                {
                    return new SubSet<>(inputSet, setBits);
                }
            };
        }

        @Override
        public boolean contains(Object obj)
        {
            if (obj instanceof Set)
            {
                Set<?> set = (Set<?>) obj;
                return inputSet.keySet()
                               .containsAll(set);
            }
            return false;
        }

        @Override
        public boolean equals(Object obj)
        {
            return super.equals(obj);
        }

        @Override
        public int hashCode()
        {
      /*
       * The sum of the sums of the hash codes in each subset is just the sum of
       * each input element's hash code times the number of sets that element
       * appears in. Each element appears in exactly half of the 2^n sets, so:
       */
            return inputSet.keySet()
                           .hashCode() << (inputSet.size() - 1);
        }
    }

    private abstract static class AbstractIndexedIterator<E>
            implements Iterator<E>
    {
        private final int size;
        private int position;

        protected abstract E get(int index);

        AbstractIndexedIterator(int size)
        {
            this(size, 0);
        }

        AbstractIndexedIterator(int size, int position)
        {
            this.size = size;
            this.position = position;
        }

        @Override
        public final boolean hasNext()
        {
            return position < size;
        }

        @Override
        public final E next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            return get(position++);
        }
    }

    private static final class SubSet<E>
            extends AbstractSet<E>
    {
        private final Map<E, Integer> inputSet;
        private final int mask;

        SubSet(Map<E, Integer> inputSet, int mask)
        {
            this.inputSet = inputSet;
            this.mask = mask;
        }

        @Override
        public Iterator<E> iterator()
        {
            return new Iterator<E>()
            {
                final List<E> elements = inputSet.keySet()
                                                 .stream()
                                                 .collect(toList());
                int remainingSetBits = mask;

                @Override
                public boolean hasNext()
                {
                    return remainingSetBits != 0;
                }

                @Override
                public E next()
                {
                    int index = Integer.numberOfTrailingZeros(remainingSetBits);
                    if (index == 32)
                    {
                        throw new NoSuchElementException();
                    }
                    remainingSetBits &= ~(1 << index);
                    return elements.get(index);
                }
            };
        }

        @Override
        public int size()
        {
            return Integer.bitCount(mask);
        }

        @Override
        public boolean contains(Object o)
        {
            Integer index = inputSet.get(o);
            return index != null && (mask & (1 << index)) != 0;
        }
    }

    private static <E> Map<E, Integer> indexMap(Collection<E> list)
    {
        final Map<E, Integer> map = new HashMap<>(list.size());
        int i = 0;
        for (E e : list)
        {
            map.put(e, i++);
        }
        return Collections.unmodifiableMap(map);
    }

    public static IntStream takeWhile(final int seed, final IntUnaryOperator f, IntPredicate p) {
        // change here
        Objects.requireNonNull(f);
        final PrimitiveIterator.OfInt iterator = new PrimitiveIterator.OfInt() {
            int t = seed;

            @Override
            public boolean hasNext() {
                return p.test(t); // change here
            }

            @Override
            public int nextInt() {
                int v = t;
                t = f.applyAsInt(t);
                return v;
            }
        };
        return StreamSupport.intStream(Spliterators.spliteratorUnknownSize(
                iterator,
                Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }
}
