package com.codebreeze.testing;

import com.google.common.base.Predicate;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.powerSet;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static org.apache.commons.lang3.RandomUtils.*;

public class Randoms {
    // XML 1.0
// #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    protected static final String XML_1_0PATTERN = "[^"
            + "\u0009\\r\\n"
            + "\u0020-\uD7FF"
            + "\uE000-\uFFFD"
            + "\ud800\udc00-\udbff\udfff"
            + "]";

    // XML 1.1
    // [#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    protected static final String XML_1_1_PATTERN = "[^"
            + "\u0001-\uD7FF"
            + "\uE000-\uFFFD"
            + "\ud800\udc00-\udbff\udfff"
            + "]+";

    private Randoms() {
    }

    private static final char[] BLANKS = {'\0', ' ', '\t', '\n', '\r', '\f'};

    public static final Supplier<Boolean> RANDOM_BOOLEAN_FACTORY = new Supplier<Boolean>() {

        @Override
        public Boolean get() {
            return randomBoolean();
        }
    };

    public static final Supplier<Integer> RANDOM_INTEGER_FACTORY = new Supplier<Integer>() {

        @Override
        public Integer get() {
            return randomInt();
        }
    };

    public static final Supplier<Long> RANDOM_LONG_FACTORY = new Supplier<Long>() {

        @Override
        public Long get() {
            return randomLong();
        }
    };

    public static final Supplier<Character> RANDOM_CHARACTER_FACTORY = new Supplier<Character>() {

        @Override
        public Character get() {
            return randomChar();
        }
    };

    public static final Supplier<DateTime> RANDOM_DATETIME_FACTORY = new Supplier<DateTime>() {
        @Override
        public DateTime get() {
            return new DateTime(randomDateBetween(new Date(Long.MIN_VALUE), new Date(Long.MAX_VALUE)));
        }
    };

    public static final Supplier<Byte> RANDOM_BYTE_FACTORY = new Supplier<Byte>() {

        @Override
        public Byte get() {
            return randomByte();
        }
    };

    public static int randomPositiveInt() {
        return nextInt(0, Integer.MAX_VALUE);
    }

    public static int randomInt() {
        return randomBoolean() ? nextInt(0, Integer.MAX_VALUE) : -nextInt(0, Integer.MAX_VALUE);
    }

    public static int randomInt(final int a, final int b) {
        return a + nextInt(0, b - a);
    }

    public static long randomLong() {
        return randomBoolean() ? nextLong(0, Long.MAX_VALUE) : -nextLong(0, Long.MAX_VALUE);
    }

    public static double randomDouble() {
        return nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public static double randomDouble(final double a, final double b) {
        return a + nextDouble(0, b - a);
    }

    public static float randomFloat() {
        return nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
    }
    public static float randomFloat(final float a, final float b) {
        return a + nextFloat(0, b - a);
    }

    public static short randomShort() {
        return (short)(Short.MIN_VALUE + nextInt(0, Short.MAX_VALUE - Short.MIN_VALUE));
    }

    public static char randomChar() {
        return randomString(1).charAt(0);
    }

    public static byte randomByte() {
        return nextBytes(1)[0];
    }

    public static int randomFrom(final int... ints) {
        Validate.isTrue(ints != null && ints.length > 0, "ints must contain values, and not be null or empty");
        return ints[nextInt(0, ints.length)];
    }

    public static <T> T randomFrom(final T... ts) {
        Validate.isTrue(ts != null && ts.length > 0, "ints must contain values, and not be null or empty");
        return ts[nextInt(0, ts.length)];
    }

    public static <T extends Enum<T>> T randomFrom(Class<T> enumClass) {
        Validate.notNull(enumClass, "enumClass cannot be null");
        return enumClass.getEnumConstants()[nextInt(0, enumClass.getEnumConstants().length)];
    }

    public static <T extends Enum<T>> Supplier<T> randomEnumFactory(final Class<T> enumClass) {
        Validate.notNull(enumClass, "enumClass cannot be null");
        return new Supplier<T>() {
            @Override
            public T get() {
                return randomFrom(enumClass);
            }
        };
    }

    /**
     * @param length length of blank string
     * @return a string composed of blanks with the length specified
     */
    public static String randomBlanks(final int length) {
        if (length == 0) {
            return "";
        } else {
            StringBuilder s = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                s.append(BLANKS[nextInt(0, BLANKS.length)]);
            }
            return s.toString();
        }
    }

    public static <S> S randomFrom(final Collection<S> collection) {
        Validate.notNull(collection, "collection cannot be null");
        Validate.notEmpty(collection, "collection cannot be empty");

        S result = null;
        final int index = nextInt(0, collection.size());
        int i = 0;
        for (final S s : collection) {
            if (i++ == index) {
                result = s;
                break;
            }
        }
        return result;
    }

    public static Date randomDateBetween(final Date from, final Date to) {
        final Calendar cal = Calendar.getInstance();

        cal.setTime(from);
        final BigDecimal decFrom = new BigDecimal(cal.getTimeInMillis());

        cal.setTime(to);
        final BigDecimal decTo = new BigDecimal(cal.getTimeInMillis());

        final BigDecimal difference = decTo.subtract(decFrom);
        final BigDecimal factor = difference.multiply(new BigDecimal(Math.random()));

        return new Date((factor.add(decFrom)).longValue());
    }

    public static DateTime randomDateBetween(final DateTime from, final DateTime to) {
        return new DateTime(randomDateBetween(from.toDate(), to.toDate()));
    }

    public static boolean randomBoolean() {
        return nextInt(0, 2) == 0;
    }

    public static String randomAlphanumeric(final int n){
        return RandomStringUtils.randomAlphanumeric(n);
    }

    public static String randomAlphabetic(final int n){
        return RandomStringUtils.randomAlphabetic(n);
    }

    public static String randomNumeric(final int n){
        return RandomStringUtils.randomNumeric(n);
    }

    public static String randomAscii(final int n){
        return RandomStringUtils.randomAscii(n);
    }

    public static String randomString(final int n){
        return random(n);
    }

    public static String randomString(final int a, final int b){
        return random(nextInt(a, b));
    }

    public  static <T> T[] randomArray(Class<T[]> clazz, final Supplier<T> factory, final int n){
        final T[] ts = clazz.cast(Array.newInstance(clazz.getComponentType(), n));
        for(int i = 0; i < ts.length; i++){
            ts[i] = factory.get();
        }
        return ts;
    }

    public static Supplier<String> randomStringFactory(final int n){
        return new Supplier<String>(){
            @Override
            public String get() {
                return randomString(n);
            }
        };
    }

    public static Supplier<String> randomNumericFactory(final int n){
        return new Supplier<String>(){
            @Override
            public String get() {
                return randomNumeric(n);
            }
        };
    }

    public static Supplier<String> randomAlphabeticFactory(final int n){
        return new Supplier<String>(){
            @Override
            public String get() {
                return randomAlphabetic(n);
            }
        };
    }

    public static Supplier<String> randomAlphanumericFactory(final int n){
        return new Supplier<String>(){
            @Override
            public String get() {
                return randomAlphanumeric(n);
            }
        };
    }

    public static Supplier<String> randomAsciiFactory(final int n){
        return new Supplier<String>(){
            @Override
            public String get() {
                return randomAlphanumeric(n);
            }
        };
    }

    public static <T> Set<T> randomSetFrom(final Collection<T> collection){
        final List<Set<T>> possibleSets = newArrayList(powerSet(newHashSet(collection)));

        return possibleSets.get(nextInt(0, possibleSets.size()));
    }

    public static <T> Set<T> randomSetFrom(final int n, final Collection<T> collection){
        final HashSet<T> inputConvertedToSet = newHashSet(collection);
        final Set<Set<T>> allPossibleCombinationsRegardLessOfSize = powerSet(inputConvertedToSet);
        final Iterable<Set<T>> removeAllIncorrectlySizedCombinations = filter(allPossibleCombinationsRegardLessOfSize, new Predicate<Set<T>>() {
            @Override
            public boolean apply(final Set<T> input) {
                return input.size() == n;
            }
        });
        final List<Set<T>> possibleSets = newArrayList(removeAllIncorrectlySizedCombinations);

        return possibleSets.get(nextInt(0, possibleSets.size()));
    }

    public static <T> T randomNotIn(final Supplier<T> tFactory, Collection<T> ts) {
        while(true){
            final T t = tFactory.get();
            if(!ts.contains(t)){
                return t;
            }
        }
    }

    public static String randomCase(final String s) {
        final StringBuilder sb = new StringBuilder(s.length());
        IntStream.range(0, s.length()).forEach(i -> sb.append(randomBoolean()
                ? Character.toUpperCase(s.charAt(i))
                : Character.toLowerCase(s.charAt(i))));
        return sb.toString();
    }

    public static String randomXml10String(final int n){
        final String result = random(n).replaceAll(XML_1_0PATTERN, "");
        final int length = result.length();
        if(length < n){
            return result + randomXml10String(n - length);
        } else {
            return result;
        }
    }

    public static String randomXml10String(final int a, final int b){
        final int count = nextInt(a, b);
        return randomXml10String(count);
    }
}

