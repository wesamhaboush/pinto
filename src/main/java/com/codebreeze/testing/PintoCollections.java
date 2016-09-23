package com.codebreeze.testing;

import java.util.*;

import static com.codebreeze.testing.PintoObjects.firstNonNull;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class PintoCollections
{
    public static <T> HashSet<T> hashSet(final T... ts)
    {
        return new HashSet<T>()
        {{
            addAll(asList(requireNonNull(ts)));
        }};
    }

    public static <T> boolean isEmpty(final Collection<T> collection)
    {
        return firstNonNull(collection, Collections.emptyList()).isEmpty();
    }

    public static <E> Set<Set<E>> powerSet(final Set<E> set)
    {
        return Collections.unmodifiableSet(new PowerSet<>(requireNonNull(set)));
    }

    private static final class PowerSet<E>
            extends AbstractSet<Set<E>>
    {
        final Map<E, Integer> inputSet;

        PowerSet(Set<E> input)
        {
            this.inputSet = PintoCollections.indexMap(input);
            if (inputSet.size() > 30)
            {
                throw new IllegalArgumentException(
                        String.format("Too many elements to create power set: %s > 30", inputSet.size()));
            }
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
            return new AbstractIndexedListIterator<Set<E>>(size())
            {
                @Override
                protected Set<E> get(final int setBits)
                {
                    return new SubSet<E>(inputSet, setBits);
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
            if (obj instanceof PowerSet)
            {
                PowerSet<?> that = (PowerSet<?>) obj;
                return inputSet.equals(that.inputSet);
            }
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

    private abstract static class AbstractIndexedListIterator<E>
            implements ListIterator<E>
    {
        private final int size;
        private int position;

        protected abstract E get(int index);

        protected AbstractIndexedListIterator(int size)
        {
            this(size, 0);
        }

        protected AbstractIndexedListIterator(int size, int position)
        {
            checkPositionIndex(position, size, "index");
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

        @Override
        public final int nextIndex()
        {
            return position;
        }

        @Override
        public final boolean hasPrevious()
        {
            return position > 0;
        }

        @Override
        public final E previous()
        {
            if (!hasPrevious())
            {
                throw new NoSuchElementException();
            }
            return get(--position);
        }

        @Override
        public final int previousIndex()
        {
            return position - 1;
        }

        @Override
        public final void add(E e)
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void set(E e)
        {
            throw new UnsupportedOperationException();
        }

        public void remove()
        {
            throw new UnsupportedOperationException("remove() is not supported");
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

    public static int checkPositionIndex(int index, int size, String desc)
    {
        // Carefully optimized for execution by hotspot (explanatory comment above)
        if (index < 0 || index > size)
        {
            throw new IndexOutOfBoundsException(badPositionIndex(index, size, desc));
        }
        return index;
    }

    private static String badPositionIndex(int index, int size, String desc)
    {
        if (index < 0)
        {
            return String.format("%s (%s) must not be negative", desc, index);
        }
        else if (size < 0)
        {
            throw new IllegalArgumentException("negative size: " + size);
        }
        else
        { // index > size
            return String.format("%s (%s) must not be greater than size (%s)", desc, index, size);
        }
    }

    public static <E> Map<E, Integer> indexMap(Collection<E> list)
    {
        final Map<E, Integer> map = new HashMap<>(list.size());
        int i = 0;
        for (E e : list)
        {
            map.put(e, i++);
        }
        return Collections.unmodifiableMap(map);
    }
}
