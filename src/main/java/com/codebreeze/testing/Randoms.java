package com.codebreeze.testing;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.codebreeze.testing.PintoCollections.powerSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class Randoms
{
    private static final Random RANDOM = new Random();

    // XML 1.0
    // #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    protected static final String XML_1_0PATTERN =
            "[^" + "\u0009\\r\\n" + "\u0020-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]";

    // XML 1.1
    // [#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    protected static final String XML_1_1_PATTERN =
            "[^" + "\u0001-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]+";

    private Randoms()
    {
        throw new UnsupportedOperationException(Randoms.class.getSimpleName() + " cannot be instantiated by design");
    }

    private static final char[] BLANKS = {'\0', ' ', '\t', '\n', '\r', '\f'};

    public static final Supplier<Boolean> RANDOM_BOOLEAN_FACTORY = () -> randomBoolean();

    public static final Supplier<Integer> RANDOM_INTEGER_FACTORY = () -> randomInt();

    public static final Supplier<Long> RANDOM_LONG_FACTORY = () -> randomLong();

    public static final Supplier<Character> RANDOM_CHARACTER_FACTORY = () -> randomChar();

    public static final Date MIN_DATE = new Date(Long.MIN_VALUE);
    public static final Date MAX_DATE = new Date(Long.MAX_VALUE);
    public static final Supplier<LocalDateTime> RANDOM_DATETIME_FACTORY = () -> LocalDateTime.from(
            Instant.ofEpochMilli(randomDateBetween(MIN_DATE, MAX_DATE).getTime())
                   .atZone(ZoneId.systemDefault()));

    public static final Supplier<Byte> RANDOM_BYTE_FACTORY = () -> randomByte();

    public static int randomPositiveInt()
    {
        return nextInt(0, Integer.MAX_VALUE);
    }

    public static int randomInt()
    {
        return randomBoolean() ? nextInt(0, Integer.MAX_VALUE) : -nextInt(0, Integer.MAX_VALUE);
    }

    public static int nextInt(final int startInclusive, final int endExclusive) {
        if(endExclusive < startInclusive)
        {
            throw new IllegalArgumentException("Start value must be smaller or equal to end value.");
        }
        if( startInclusive < 0)
        {
            throw new IllegalArgumentException("Both range values must be non-negative.");
        }

        if (startInclusive == endExclusive)
        {
            return startInclusive;
        }

        return startInclusive + RANDOM.nextInt(endExclusive - startInclusive);
    }

    public static int randomInt(final int a, final int b)
    {
        return a + nextInt(0, b - a);
    }

    public static long randomLong()
    {
        return randomBoolean() ? nextLong(0, Long.MAX_VALUE) : -nextLong(0, Long.MAX_VALUE);
    }

    public static long nextLong(final long startInclusive, final long endExclusive) {
        if(endExclusive < startInclusive)
        {
            throw new IllegalArgumentException("Start value must be smaller or equal to end value.");
        }
        if( startInclusive < 0)
        {
            throw new IllegalArgumentException("Both range values must be non-negative.");
        }

        if (startInclusive == endExclusive)
        {
            return startInclusive;
        }

        return (long) nextDouble(startInclusive, endExclusive);
    }

    public static double nextDouble(final double startInclusive, final double endInclusive) {
        if(endInclusive < startInclusive)
        {
            throw new IllegalArgumentException("Start value must be smaller or equal to end value.");
        }
        if( startInclusive < 0)
        {
            throw new IllegalArgumentException("Both range values must be non-negative.");
        }
        if (startInclusive == endInclusive)
        {
            return startInclusive;
        }

        return startInclusive + ((endInclusive - startInclusive) * RANDOM.nextDouble());
    }

    public static double randomDouble()
    {
        return nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public static double randomDouble(final double a, final double b)
    {
        return a + nextDouble(0, b - a);
    }

    public static float randomFloat()
    {
        return nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public static float nextFloat(final float startInclusive, final float endInclusive) {
        if(endInclusive < startInclusive)
        {
            throw new IllegalArgumentException("Start value must be smaller or equal to end value.");
        }

        if( startInclusive < 0)
        {
            throw new IllegalArgumentException("Both range values must be non-negative.");
        }

        if (startInclusive == endInclusive) {
            return startInclusive;
        }

        return startInclusive + ((endInclusive - startInclusive) * RANDOM.nextFloat());
    }

    public static float randomFloat(final float a, final float b)
    {
        return a + nextFloat(0, b - a);
    }

    public static short randomShort()
    {
        return (short) (Short.MIN_VALUE + nextInt(0, Short.MAX_VALUE - Short.MIN_VALUE));
    }

    public static char randomChar()
    {
        return randomString(1).charAt(0);
    }

    public static byte randomByte()
    {
        return nextBytes(1)[0];
    }

    public static byte[] nextBytes(final int count) {
        if(count < 0)
        {
            throw new IllegalArgumentException("Count cannot be negative");
        }

        final byte[] result = new byte[count];
        RANDOM.nextBytes(result);
        return result;
    }

    public static int randomFrom(final int... ints)
    {
        Objects.requireNonNull(ints, "cannot handle null set of ints");
        if( ints.length == 0 ) {
            throw new IllegalArgumentException("cannot handle empty set of ints");
        }
        return ints[nextInt(0, ints.length)];
    }

    public static <T> T randomFrom(final T... ts)
    {
        Objects.requireNonNull(ts, "cannot handle null set of objects");
        if( ts.length == 0 ) {
            throw new IllegalArgumentException("cannot handle empty set of objects");
        }
        return ts[nextInt(0, ts.length)];
    }

    public static <T extends Enum<T>> T randomFrom(Class<T> enumClass)
    {
        Objects.requireNonNull(enumClass, "enum class cannot be null");
        return enumClass.getEnumConstants()[nextInt(0, enumClass.getEnumConstants().length)];
    }

    public static <T extends Enum<T>> Supplier<T> randomEnumFactory(final Class<T> enumClass)
    {
        Objects.requireNonNull(enumClass, "enum class cannot be null");
        return () -> randomFrom(enumClass);
    }

    /**
     * @param length length of blank string
     * @return a string composed of blanks with the length specified
     */
    public static String randomBlanks(final int length)
    {
        if (length == 0)
        {
            return "";
        }
        else
        {
            StringBuilder s = new StringBuilder(length);
            for (int i = 0; i < length; i++)
            {
                s.append(BLANKS[nextInt(0, BLANKS.length)]);
            }
            return s.toString();
        }
    }

    public static <S> S randomFrom(final Collection<S> collection)
    {
        Objects.requireNonNull(collection, "collection cannot be null");
        if(collection.isEmpty())
        {
            throw new IllegalArgumentException("cannot pick an element from an empty collection");
        }

        final int index = nextInt(0, collection.size());
        final Iterator<S> iterator = collection.iterator();
        int i = 0;
        while(i++ != index) {
            iterator.next();
        }
        return iterator.next();
    }

    public static Date randomDateBetween(final Date from, final Date to)
    {
        final Calendar cal = Calendar.getInstance();

        cal.setTime(from);
        final BigDecimal decFrom = new BigDecimal(cal.getTimeInMillis());

        cal.setTime(to);
        final BigDecimal decTo = new BigDecimal(cal.getTimeInMillis());

        final BigDecimal difference = decTo.subtract(decFrom);
        final BigDecimal factor = difference.multiply(new BigDecimal(Math.random()));

        return new Date((factor.add(decFrom)).longValue());
    }

    public static LocalDateTime randomDateBetween(final LocalDateTime from, final LocalDateTime to)
    {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(randomDateBetween(Date.from(
                from.atZone(ZoneId.systemDefault())
                    .toInstant()), Date.from(to.atZone(ZoneId.systemDefault())
                                               .toInstant())).getTime()), ZoneId.systemDefault());
    }

    public static boolean randomBoolean()
    {
        return nextInt(0, 2) == 0;
    }

    public static String randomString(final int a, final int b)
    {
        return randomString(nextInt(a, b));
    }

    public static <T> T[] randomArray(Class<T[]> clazz, final Supplier<T> factory, final int n)
    {
        final T[] ts = clazz.cast(Array.newInstance(clazz.getComponentType(), n));
        for (int i = 0; i < ts.length; i++)
        {
            ts[i] = factory.get();
        }
        return ts;
    }

    public static Supplier<String> randomStringFactory(final int n)
    {
        return () -> randomString(n);
    }

    public static Supplier<String> randomNumericFactory(final int n)
    {
        return () -> randomNumeric(n);
    }

    public static Supplier<String> randomAlphabeticFactory(final int n)
    {
        return () -> randomAlphabetic(n);
    }

    public static Supplier<String> randomAlphanumericFactory(final int n)
    {
        return () -> randomAlphanumeric(n);
    }

    public static Supplier<String> randomAsciiFactory(final int n)
    {
        return () -> randomAscii(n);
    }

    public static <T> Set<T> randomSetFrom(final Collection<T> collection)
    {
        final Set<T> collect = collection.stream()
                                         .collect(toSet());
        final List<Set<T>> possibleSets = powerSet(collect).stream()
                                                           .collect(toList());

        return possibleSets.get(nextInt(0, possibleSets.size()));
    }

    public static <T> Set<T> randomSetFrom(final int n, final Collection<T> collection)
    {
        PintoCheck.Argument.notLessThan(collection.size(), n,
                                        "cannot produce a set of n items from a collection that has a size smaller than n");
        final Set<T> inputConvertedToSet = collection.stream().collect(toSet());
        final Set<Set<T>> allPossibleCombinationsRegardLessOfSize = powerSet(inputConvertedToSet);
        final List<Set<T>> removeAllIncorrectlySizedCombinations = allPossibleCombinationsRegardLessOfSize
                .stream()
                .filter(input -> input.size() == n)
                .collect(toList());

        final int index = nextInt(0, removeAllIncorrectlySizedCombinations.size());
        return removeAllIncorrectlySizedCombinations.get(index);
    }

    public static <T> T randomNotIn(final Supplier<T> tFactory, Collection<T> ts)
    {
        while (true)
        {
            final T t = tFactory.get();
            if (!ts.contains(t))
            {
                return t;
            }
        }
    }

    public static String randomCase(final String s)
    {
        final StringBuilder sb = new StringBuilder(s.length());
        IntStream.range(0, s.length())
                 .forEach(i -> sb.append(
                         randomBoolean() ? Character.toUpperCase(s.charAt(i)) : Character.toLowerCase(s.charAt(i))));
        return sb.toString();
    }

    public static String randomXml10String(final int n)
    {
        final String result = randomString(n).replaceAll(XML_1_0PATTERN, "");
        final int length = result.length();
        if (length < n)
        {
            return result + randomXml10String(n - length);
        }
        else
        {
            return result;
        }
    }

    public static String randomXml10String(final int a, final int b)
    {
        final int count = nextInt(a, b);
        return randomXml10String(count);
    }

    public static String randomString(final int count) {
        return randomString(count, false, false);
    }

    public static String randomAscii(final int count) {
        return randomString(count, 32, 127, false, false);
    }

    public static String randomAlphabetic(final int count) {
        return randomString(count, true, false);
    }

    public static String randomAlphanumeric(final int count) {
        return randomString(count, true, true);
    }

    public static String randomNumeric(final int count) {
        return randomString(count, false, true);
    }

    public static String randomString(final int count, final boolean letters, final boolean numbers) {
        return randomString(count, 0, 0, letters, numbers);
    }

    public static String randomString(final int count, final int start, final int end, final boolean letters, final boolean numbers) {
        return randomString(count, start, end, letters, numbers, null, RANDOM);
    }

    public static String randomString(final int count, final int start, final int end, final boolean letters, final boolean numbers, final char... chars) {
        return randomString(count, start, end, letters, numbers, chars, RANDOM);
    }

    public static String randomString(int count, int start, int end, final boolean letters, final boolean numbers,
                                final char[] chars, final Random random) {
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested randomString string length " + count + " is less than 0.");
        }
        if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        }

        if (start == 0 && end == 0) {
            if (chars != null) {
                end = chars.length;
            } else {
                if (!letters && !numbers) {
                    end = Integer.MAX_VALUE;
                } else {
                    end = 'z' + 1;
                    start = ' ';
                }
            }
        } else {
            if (end <= start) {
                throw new IllegalArgumentException("Parameter end (" + end + ") must be greater than start (" + start + ")");
            }
        }

        final char[] buffer = new char[count];
        final int gap = end - start;

        while (count-- != 0) {
            char ch;
            if (chars == null) {
                ch = (char) (random.nextInt(gap) + start);
            } else {
                ch = chars[random.nextInt(gap) + start];
            }
            if (letters && Character.isLetter(ch)
                || numbers && Character.isDigit(ch)
                || !letters && !numbers) {
                if(ch >= 56320 && ch <= 57343) {
                    if(count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if(ch >= 55296 && ch <= 56191) {
                    if(count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if(ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }

    public static String randomString(final int count, final String chars) {
        if (chars == null) {
            return randomString(count, 0, 0, false, false, null, RANDOM);
        }
        return randomString(count, chars.toCharArray());
    }

    public static String randomString(final int count, final char... chars) {
        if (chars == null) {
            return randomString(count, 0, 0, false, false, null, RANDOM);
        }
        return randomString(count, 0, chars.length, false, false, chars, RANDOM);
    }
}
