package com.codebreeze.testing;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.codebreeze.testing.Randoms.randomBoolean;
import static com.codebreeze.testing.Randoms.randomFrom;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class RandomsTest {
    @Test
    public void testRandomFromEnum() {
        //then
        //if five consecutive elements are the same, I suspect it is not random!
        assertTrue(
                newHashSet(
                        randomFrom(TestEnum.class),
                        randomFrom(TestEnum.class),
                        randomFrom(TestEnum.class),
                        randomFrom(TestEnum.class),
                        randomFrom(TestEnum.class)
                ).size() > 1);
    }

    @Test
    public void testRandomFromArray() {
        //given
        final Object[] obs = {new Object(), new Object(), new Object()};

        //then
        //if five consecutive elements are the same, I suspect it is not random!
        assertTrue(Iterables.any(newArrayList(
                newHashSet(randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs) ).size() > 1,
                newHashSet(randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs) ).size() > 1,
                newHashSet(randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs) ).size() > 1),
                Predicates.equalTo(Boolean.TRUE)));
    }

    @Test
    public void testRandomFromBoolean() {
        //given

        //then
        //if 10 consecutive elements are the same, I suspect it is not random!
        final HashSet<Boolean> booleans = newHashSet(
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean(),
                randomBoolean()
        );
        assertTrue("random number was not very random ? here is the generated set: " + booleans, booleans.size() > 1);
    }

    @Test
    public void testRandomFloatBetween() {
        final float value = Randoms.randomFloat(-1.0f, 20.0f);
        assertTrue(value < 20.0f && value > -1.0f);

        //chances of a randomizer producing the same exact values 4 consecutive times should be infinitisimal
        assertFalse(newHashSet(asList(
                Randoms.randomFloat(-1.0f, 20.0f),
                Randoms.randomFloat(-1.0f, 20.0f),
                Randoms.randomFloat(-1.0f, 20.0f),
                Randoms.randomFloat(-1.0f, 20.0f))).size() == 1);
    }

    @Test
    public void testRandomDoubleBetween() {
        final double value = Randoms.randomDouble(-1.0, 20.0);
        assertTrue(value < 20.0 && value > -1.0);

        //chances of a randomizer producing the same exact values 4 consecutive times should be infinitisimal
        assertFalse(newHashSet(asList(
                Randoms.randomDouble(-1.0f, 20.0f),
                Randoms.randomDouble(-1.0f, 20.0f),
                Randoms.randomDouble(-1.0f, 20.0f),
                Randoms.randomDouble(-1.0f, 20.0f))).size() == 1);
    }

    @Test
    public void testRandomPositiveInt() {
        assertTrue(Randoms.randomPositiveInt() > 0);
        assertFalse(newHashSet(asList(
                Randoms.randomPositiveInt(),
                Randoms.randomPositiveInt(),
                Randoms.randomPositiveInt(),
                Randoms.randomPositiveInt())).size() == 1);
    }

    @Test
    public void testRandomInt() {
        assertFalse(newHashSet(asList(
                Randoms.randomInt(),
                Randoms.randomInt(),
                Randoms.randomInt(),
                Randoms.randomInt())).size() == 1);
    }

    @Test
    public void testRandomLong() {
        assertFalse(newHashSet(asList(
                Randoms.randomLong(),
                Randoms.randomLong(),
                Randoms.randomLong(),
                Randoms.randomLong())).size() == 1);
    }

    @Test
    public void testRandomDouble() {
        assertFalse(newHashSet(asList(
                Randoms.randomDouble(),
                Randoms.randomDouble(),
                Randoms.randomDouble(),
                Randoms.randomDouble())).size() == 1);
    }

    @Test
    public void testRandomShort() {
        assertFalse(newHashSet(asList(
                Randoms.randomShort(),
                Randoms.randomShort(),
                Randoms.randomShort(),
                Randoms.randomShort())).size() == 1);
    }

    @Test
    public void testRandomChar() {
        assertFalse(newHashSet(asList(
                Randoms.randomChar(),
                Randoms.randomChar(),
                Randoms.randomChar(),
                Randoms.randomChar())).size() == 1);
    }

    @Test
    public void testRandomByte() {
        assertFalse(newHashSet(asList(
                Randoms.randomByte(),
                Randoms.randomByte(),
                Randoms.randomByte(),
                Randoms.randomByte())).size() == 1);
    }

    @Test
    public void testRandomIntBetween() {
        final int value = Randoms.randomInt(-500, 1500);
        assertTrue(value < 1500 && value > -500);

        //chances of a randomizer producing the same exact values 4 consecutive times should be infinitisimal
        assertFalse(newHashSet(asList(
                Randoms.randomInt(-500, 1500),
                Randoms.randomInt(-500, 1500),
                Randoms.randomInt(-500, 1500),
                Randoms.randomInt(-500, 1500))).size() == 1);
    }

    /**
     * after five seconds of trying on the random generator to get different
     * values, I am doubtful this is random
     */
    @Test(timeout = 5000)
    public void testRandomBoolean() {
        //chances of a randomizer producing the same exact value many many consecutive times should be infinitisimal
        final Set<Boolean> booleans = newHashSet();
        final Runnable code = new Runnable() {

            @Override
            public void run() {
                while (booleans.size() <= 1) {
                    booleans.add(randomBoolean());
                }
            }
        };

        final String message = String.format("code ran for with result [%s], " +
                        "something is not random here", booleans.toString());
        assertFalse(message, booleans.size() == 1);
    }

    @Test
    public void testRandomFrom() {
        //one item list
        final int[] oneItemArray = new int[]{876};
        assertEquals(876, randomFrom(oneItemArray));
        assertEquals(876, randomFrom(oneItemArray));
        assertEquals(876, randomFrom(oneItemArray));

        //multi item list
        final int[] fourItemArray = new int[]{876, 877, 878, 879};
        final Collection<Integer> allItems = newArrayList(876, 877, 878, 879);
        final Collection<Integer> foundItems = newArrayList();

        int count = 0;
        while (!allItems.equals(foundItems) && count < 1000) {
            final int nextValue = randomFrom(fourItemArray);
            assertTrue(allItems.contains(nextValue));
            foundItems.add(nextValue);
            count += 1;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testRandomFromExceptionCaseWithNull() {
        int[] nullIntsArray = null;

        //exception cases
        randomFrom(nullIntsArray);
    }

    @Test(expected = RuntimeException.class)
    public void testRandomFromExceptionCaseWithEmptyArrayF() {
        //exception cases
        randomFrom(new int[]{});
    }

    @Test
    public void testRandomAlphanum() {
        final Random r = new Random(System.currentTimeMillis());
        final int length = r.nextInt(20) + 1;
        //chances of a randomizer producing the same exact values 4 consecutive times should be infinitisimal
        assertFalse(newHashSet(asList(
                Randoms.randomAlphanumeric(length),
                Randoms.randomAlphanumeric(length),
                Randoms.randomAlphanumeric(length),
                Randoms.randomAlphanumeric(length))).size() == 1);
    }


    @Test
    public void testRandomFloat() {
        assertFalse(newHashSet(asList(
                Randoms.randomFloat(),
                Randoms.randomFloat(),
                Randoms.randomFloat(),
                Randoms.randomFloat())).size() == 1);
    }

    @Test
    public void testRandomCombinationOf() {
        final Supplier<? extends Collection<Dummy>> factory = () -> new ArrayList<Dummy>();
        assertFalse(newHashSet(asList(
                randomFrom(Dummy.values()),
                randomFrom(factory, Dummy.values()),
                randomFrom(factory, Dummy.values()),
                randomFrom(factory, Dummy.values()))).size() == 1);
    }

    @Test
    public void testRandomStringWithTwoInts() {
        assertFalse(newHashSet(asList(
                Randoms.randomString(1, 10),
                Randoms.randomString(1, 10),
                Randoms.randomString(1, 10),
                Randoms.randomString(1, 10))).size() == 1);

        assertFalse(newHashSet(asList(
                Randoms.randomString(1, 10).length(),
                Randoms.randomString(1, 10).length(),
                Randoms.randomString(1, 10).length(),
                Randoms.randomString(1, 10).length())).size() == 1);
    }

    @Test
    public void testRandomStringWithOneInt() {
        assertFalse(newHashSet(asList(
                Randoms.randomString(10),
                Randoms.randomString(10),
                Randoms.randomString(10),
                Randoms.randomString(10))).size() == 1);
    }

    @Test
    public void testRandomFromWithCollections(){
        //given
        final Collection<String> coll = newArrayList("a","b","c");

        //when
        final String result = randomFrom(coll);

        //then
        assertNotNull(result);
        assertTrue(coll.contains(result));
        while(true){
            if(randomFrom(coll).equals(randomFrom(coll))){
                break;
            }
        }
    }


    @Test
    public void testRandomFromWithArrays(){
        //given
        final String[] array = {"a","b","c"};

        //when
        final String result = randomFrom(array);

        //then
        assertNotNull(result);
        assertTrue(asList(array).contains(result));
        while(true){
            if(!randomFrom(array).equals(randomFrom(array))){
                break;
            }
        }
    }

    @Test
    public void testRandomDateBetween(){
        //given
        final Date first = new Date();
        //50 days ahead
        final Date second = new Date(first.getTime() + (50 * 24 * 60 * 60 * 1000));

        //when
        final Date randomDate = Randoms.randomDateBetween(first, second);

        //then
        assertTrue(String.format("date[%s] is not between lower[%s] and upper[%s]", randomDate, first, second),
                randomDate.getTime() >= first.getTime() && randomDate.getTime() <= second.getTime());
    }

    @Test
    public void testRandomDateBetween_DateTime(){
        //given
        final DateTime first = new DateTime();
        //50 days ahead
        final DateTime second = new DateTime(first.plusDays(50));

        //when
        final DateTime randomDate = Randoms.randomDateBetween(first, second);

        //then
        assertTrue(String.format("dateTime[%s] is not between lower[%s] and upper[%s]", randomDate, first, second),
                randomDate.toDate().getTime() >= first.toDate().getTime() && randomDate.toDate().getTime() <= second.toDate().getTime());
    }

    @Test
    public void testRandomNotInForStringsGoodFactory(){
        //given
        final Supplier<String> stringFactory = new Supplier<String>() {
            private final String[] values = {"1", "2", "3", "4", "5"};
            private final CyclicIndex cyclicIndex = new CyclicIndex(values.length);
            @Override
            public String get() {
                return values[cyclicIndex.next()];
            }
        };

        final Collection<String> unwantedValues = newArrayList("1", "2", "3", "4");

        //when
        final String wantedValue = Randoms.randomNotIn(stringFactory, unwantedValues);

        //then
        assertEquals("5", wantedValue);
    }

    @Test
    public void testRandomNotInForIntegersGoodFactory(){
        //given
        final Supplier<Integer> stringFactory = new Supplier<Integer>() {
            private final Integer[] values = {1, 2, 3, 4, 5};
            private final CyclicIndex cyclicIndex = new CyclicIndex(values.length);
            @Override
            public Integer get() {
                return values[cyclicIndex.next()];
            }
        };

        final Collection<Integer> unwantedValues = newArrayList(1, 2, 3, 4);

        //when
        final int wantedValue = Randoms.randomNotIn(stringFactory, unwantedValues);

        //then
        assertEquals(5, wantedValue);
    }

    @Test
    public void testRandomNotInForIntegersGoodFactoryFirstValueReturned(){
        //given
        final Supplier<Integer> stringFactory = new Supplier<Integer>() {
            private final Integer[] values = {1, 2, 3, 4, 5};
            private final CyclicIndex cyclicIndex = new CyclicIndex(values.length);
            @Override
            public Integer get() {
                return values[cyclicIndex.next()];
            }
        };

        final Collection<Integer> unwantedValues = newArrayList(2, 3, 4, 5);

        //when
        final int wantedValue = Randoms.randomNotIn(stringFactory, unwantedValues);

        //then
        assertEquals(1, wantedValue);
    }

    @Test
    public void testRandomDateFactory(){
        IntStream.range(0, 100).forEach(i -> assertNotNull(Randoms.RANDOM_DATETIME_FACTORY.get()));
    }

    @Test
    public void testRandomCaseDoesRandomizeCases(){
        //given
        final TestCasingEnum enumMember = randomFrom(TestCasingEnum.class);

        //when
        final Set<String> randomCaseSet = Sets.newSet(
                Randoms.randomCase(enumMember.name()),
                Randoms.randomCase(enumMember.name()),
                Randoms.randomCase(enumMember.name()),
                Randoms.randomCase(enumMember.name()),
                Randoms.randomCase(enumMember.name()),
                Randoms.randomCase(enumMember.name()),
                Randoms.randomCase(enumMember.name())
        );
        final Set<String> toLowerCase = toLowerCase(randomCaseSet);

        //then
        assertTrue(randomCaseSet.toString(), randomCaseSet.size() > 1);
        assertTrue(randomCaseSet.toString(), toLowerCase.size() == 1);
        assertTrue(randomCaseSet.toString(), toLowerCase.contains(enumMember.name().toLowerCase()));
    }

    @Test
    public void testRandomXml10String(){
        assertEquals("", Randoms.randomXml10String(0));

        assertFalse(Randoms.randomXml10String(100).matches(Randoms.XML_1_0PATTERN));
        assertFalse(Randoms.randomXml10String(0, 100).matches(Randoms.XML_1_0PATTERN));
    }

    //utils

    private Set<String> toLowerCase(final Set<String> set) {
        return newHashSet(transform(newArrayList(set), toLowerCase()));
    }

    private Function<String, String> toLowerCase() {
        return input -> input.toLowerCase();
    }

    //test utils

    private static enum Dummy {

        A, B, C
    }

    //test
    private enum TestEnum {
        A, B, C;
    }

    private enum TestCasingEnum {
        JESUS, MOSES, BUDDHA;
    }
}
