package com.codebreeze.testing;

import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.codebreeze.testing.PintoCollections.hashSet;
import static com.codebreeze.testing.Randoms.randomBoolean;
import static com.codebreeze.testing.Randoms.randomFrom;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.*;

public class RandomsTest
{

    @Test
    public void randoms_class_cannot_be_instantiated()
    {
        //when
        final Throwable throwable = catchThrowable(() -> Randoms.class.newInstance());

        //then
        assertThat(throwable).isInstanceOf(IllegalAccessException.class);
    }

    @Test
    public void randoms_class_cannot_be_instantiated_even_through_reflection()
    {
        //given
        final Constructor<?> noArgsConstructor = Randoms.class.getDeclaredConstructors()[0];
        noArgsConstructor.setAccessible(true);

        //when
        final Throwable throwable = catchThrowable(() -> noArgsConstructor.newInstance());

        //then
        assertThat(throwable).isInstanceOf(InvocationTargetException.class)
                             .hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void random_boolean_factory_produces_different_results()
    {
        final Boolean aBoolean = Randoms.RANDOM_BOOLEAN_FACTORY.get();
        //it must exit cz it shouldn't always produce the same value
        while (aBoolean.booleanValue() == Randoms.RANDOM_BOOLEAN_FACTORY.get()
                                                                        .booleanValue())
        {
            //no-op
        }
    }

    @Test
    public void random_integer_factory_produces_different_results()
    {
        final Integer anInteger = Randoms.RANDOM_INTEGER_FACTORY.get();
        //it must exit cz it shouldn't always produce the same value
        while (anInteger.intValue() == Randoms.RANDOM_INTEGER_FACTORY.get()
                                                                     .intValue())
        {
            //no-op
        }
    }

    @Test
    public void random_long_factory_produces_different_results()
    {
        final Long aLong = Randoms.RANDOM_LONG_FACTORY.get();
        //it must exit cz it shouldn't always produce the same value
        while (aLong.longValue() == Randoms.RANDOM_LONG_FACTORY.get()
                                                               .longValue())
        {
            //no-op
        }
    }

    @Test
    public void random_character_factory_produces_different_results()
    {
        final Character aCharacter = Randoms.RANDOM_CHARACTER_FACTORY.get();
        //it must exit cz it shouldn't always produce the same value
        while (aCharacter.charValue() == Randoms.RANDOM_CHARACTER_FACTORY.get()
                                                                         .charValue())
        {
            //no-op
        }
    }

    @Test
    public void random_character_produces_no_exception()
    {
        IntStream.range(0, 300).forEach(i -> Randoms.randomChar());
    }

    @Test
    public void random_string_respects_size_requirements()
    {
        String s1 = null;
        char[] c1 = null;
        assertThat(Randoms.randomString(100, s1)).hasSize(100);
        assertThat(Randoms.randomString(100, c1)).hasSize(100);
        String s2 = "abc";
        assertThat(Randoms.randomString(100, s2)).hasSize(100).matches("(a|b|c){100}");
    }

    @Test
    public void random_byte_factory_produces_different_results()
    {
        final Byte aByte = Randoms.RANDOM_BYTE_FACTORY.get();
        //it must exit cz it shouldn't always produce the same value
        while (aByte.byteValue() == Randoms.RANDOM_BYTE_FACTORY.get()
                                                               .byteValue())
        {
            //no-op
        }
    }

    @Test
    public void next_int_should_return_same_value_if_inputs_are_same()
    {
        IntStream.range(0, 10)
                 .forEach(
                         i -> assertThat(Randoms.nextInt(i, i)).isEqualTo(i)
                         );
    }

    @Test
    public void next_int_should_reject_values_with_end_less_then_start()
    {
        //given
        final int start = 9;
        //when
        final Throwable thrown = catchThrowable(() -> Randoms.nextInt(start, start - 1));
        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void next_int_should_reject_values_with_start_less_zero()
    {
        //given
        final int start = 9;
        //when
        final Throwable thrown = catchThrowable(() -> Randoms.nextInt(-start, start - 1));
        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void next_long_should_return_same_value_if_inputs_are_same()
    {
        IntStream.range(0, 10)
                 .forEach(
                         i -> assertThat(Randoms.nextLong(i, i)).isEqualTo(i)
                         );
    }

    @Test
    public void next_long_should_reject_values_with_end_less_then_start()
    {
        //given
        final long start = 9;
        //when
        final Throwable thrown = catchThrowable(() -> Randoms.nextLong(start, start - 1));
        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void next_long_should_reject_values_with_start_less_than_zero()
    {
        //given
        final long start = -1;
        //when
        final Throwable thrown = catchThrowable(() -> Randoms.nextLong(start, start + 50));
        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void next_double_should_return_same_value_if_inputs_are_same()
    {
        IntStream.range(0, 10)
                 .forEach(
                         i -> assertThat(Randoms.nextDouble(i, i)).isEqualTo(i)
                         );
    }

    @Test
    public void next_double_should_reject_values_with_end_less_then_start()
    {
        //given
        final double start = 9;
        //when
        final Throwable thrown = catchThrowable(() -> Randoms.nextDouble(start, start - 1));
        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void next_double_should_reject_values_with_start_less_than_zero()
    {
        //given
        final double start = -1;
        //when
        final Throwable thrown = catchThrowable(() -> Randoms.nextDouble(start, start + 50));
        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void next_float_should_return_same_value_if_inputs_are_same()
    {
        IntStream.range(0, 10)
                 .forEach(
                         i -> assertThat(Randoms.nextFloat(i, i)).isEqualTo(i)
                         );
    }

    @Test
    public void next_float_should_reject_values_with_end_less_then_start()
    {
        //given
        final float start = 9;
        //when
        final Throwable thrown = catchThrowable(() -> Randoms.nextFloat(start, start - 1));
        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void next_float_should_reject_values_with_start_less_than_zero()
    {
        //given
        final float start = -1;
        //when
        final Throwable thrown = catchThrowable(() -> Randoms.nextFloat(start, start + 50));
        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void next_bytes_should_reject_values_with_count_less_then_start()
    {
        //given
        //when
        final Throwable thrown = catchThrowable(() -> Randoms.nextBytes(-1));
        //then
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testRandomFromEnum()
    {
        //then
        //if five consecutive elements are the same, I suspect it is not randomString!
        assertTrue(hashSet(randomFrom(TestEnum.class), randomFrom(TestEnum.class), randomFrom(TestEnum.class),
                           randomFrom(TestEnum.class), randomFrom(TestEnum.class)).size() > 1);
    }

    @Test
    public void testRandomFromArray()
    {
        //given
        final Object[] obs = {new Object(), new Object(), new Object()};

        //then
        //if five consecutive elements are the same, I suspect it is not randomString!
        assertTrue(Stream.of(hashSet(randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs),
                                  randomFrom(obs)).size() > 1,
                          hashSet(randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs),
                                  randomFrom(obs)).size() > 1,
                          hashSet(randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs),
                                  randomFrom(obs)).size() > 1)
                                                              .anyMatch(i -> i));
    }

    @Test
    public void testRandomFromCollection()
    {
        //given
        final List<Object> obs = asList(new Object(), new Object(), new Object());

        //then
        //if five consecutive elements are the same, I suspect it is not random!
        assertTrue(Stream.of(hashSet(randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs),
                                     randomFrom(obs)).size() > 1,
                          hashSet(randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs),
                                  randomFrom(obs)).size() > 1,
                          hashSet(randomFrom(obs), randomFrom(obs), randomFrom(obs), randomFrom(obs),
                                  randomFrom(obs)).size() > 1)
                         .anyMatch(i -> i));
    }

    @Test
    public void random_from_array_should_not_accept_empty_arrays()
    {
        assertThat(catchThrowable(() -> Randoms.randomFrom(new String[]{})))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void random_from_collection_should_not_accept_empty_collections()
    {
        assertThat(catchThrowable(() -> Randoms.randomFrom(Collections.emptyList())))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void random_blanks_should_return_blank_values()
    {
        assertThat(Randoms.randomBlanks(0)).isEmpty();
        assertThat(Randoms.randomBlanks(5))
                .hasSize(5)
                .matches(s -> s.trim().length() == 0);
    }

    @Test
    public void testRandomFromBoolean()
    {
        //given

        //then
        //if 10 consecutive elements are the same, I suspect it is not randomString!
        final Set<Boolean> booleans = hashSet(randomBoolean(), randomBoolean(), randomBoolean(), randomBoolean(),
                                              randomBoolean(), randomBoolean(), randomBoolean(), randomBoolean(),
                                              randomBoolean(), randomBoolean());
        assertTrue("randomString number was not very randomString ? here is the generated set: " + booleans,
                   booleans.size() > 1);
    }

    @Test
    public void testRandomFloatBetween()
    {
        final float value = Randoms.randomFloat(-1.0f, 20.0f);
        assertTrue(value < 20.0f && value > -1.0f);

        //chances of a randomizer producing the same exact values 4 consecutive times should be infinitisimal
        assertFalse(hashSet(Randoms.randomFloat(-1.0f, 20.0f), Randoms.randomFloat(-1.0f, 20.0f),
                            Randoms.randomFloat(-1.0f, 20.0f), Randoms.randomFloat(-1.0f, 20.0f)).size() == 1);
    }

    @Test
    public void testRandomDoubleBetween()
    {
        final double value = Randoms.randomDouble(-1.0, 20.0);
        assertTrue(value < 20.0 && value > -1.0);

        //chances of a randomizer producing the same exact values 4 consecutive times should be infinitisimal
        assertFalse(hashSet(Randoms.randomDouble(-1.0f, 20.0f), Randoms.randomDouble(-1.0f, 20.0f),
                            Randoms.randomDouble(-1.0f, 20.0f), Randoms.randomDouble(-1.0f, 20.0f)).size() == 1);
    }

    @Test
    public void testRandomPositiveInt()
    {
        assertTrue(Randoms.randomPositiveInt() > 0);
        assertFalse(hashSet(Randoms.randomPositiveInt(), Randoms.randomPositiveInt(), Randoms.randomPositiveInt(),
                            Randoms.randomPositiveInt()).size() == 1);
    }

    @Test
    public void testRandomInt()
    {
        assertFalse(hashSet(Randoms.randomInt(), Randoms.randomInt(), Randoms.randomInt(),
                            Randoms.randomInt()).size() == 1);
    }

    @Test
    public void testRandomLong()
    {
        assertFalse(hashSet(Randoms.randomLong(), Randoms.randomLong(), Randoms.randomLong(),
                            Randoms.randomLong()).size() == 1);
    }

    @Test
    public void testRandomDouble()
    {
        assertFalse(hashSet(Randoms.randomDouble(), Randoms.randomDouble(), Randoms.randomDouble(),
                            Randoms.randomDouble()).size() == 1);
    }

    @Test
    public void testRandomShort()
    {
        assertFalse(hashSet(Randoms.randomShort(), Randoms.randomShort(), Randoms.randomShort(),
                            Randoms.randomShort()).size() == 1);
    }

    @Test
    public void testRandomChar()
    {
        assertFalse(hashSet(Randoms.randomChar(), Randoms.randomChar(), Randoms.randomChar(),
                            Randoms.randomChar()).size() == 1);
    }

    @Test
    public void testRandomByte()
    {
        assertFalse(hashSet(Randoms.randomByte(), Randoms.randomByte(), Randoms.randomByte(),
                            Randoms.randomByte()).size() == 1);
    }

    @Test
    public void testRandomIntBetween()
    {
        final int value = Randoms.randomInt(-500, 1500);
        assertTrue(value < 1500 && value > -500);

        //chances of a randomizer producing the same exact values 4 consecutive times should be infinitisimal
        assertFalse(hashSet(Randoms.randomInt(-500, 1500), Randoms.randomInt(-500, 1500), Randoms.randomInt(-500, 1500),
                            Randoms.randomInt(-500, 1500)).size() == 1);
    }

    /**
     * after five seconds of trying on the randomString generator to get different
     * values, I am doubtful this is randomString
     */
    @Test(timeout = 5000)
    public void testRandomBoolean()
    {
        //chances of a randomizer producing the same exact value many many consecutive times should be infinitisimal
        final Set<Boolean> booleans = hashSet();
        final Runnable code = () ->
        {
            while (booleans.size() <= 1)
            {
                booleans.add(randomBoolean());
            }
        };

        code.run();

        final String message = String.format("code ran for with result [%s], " + "something is not randomString here",
                                             booleans.toString());
        assertFalse(message, booleans.size() == 1);
    }

    @Test
    public void testRandomFrom()
    {
        //one item list
        final int[] oneItemArray = new int[]{876};
        assertEquals(876, randomFrom(oneItemArray));
        assertEquals(876, randomFrom(oneItemArray));
        assertEquals(876, randomFrom(oneItemArray));

        //multi item list
        final int[] fourItemArray = new int[]{876, 877, 878, 879};
        final Collection<Integer> allItems = asList(876, 877, 878, 879);
        final Collection<Integer> foundItems = hashSet();

        int count = 0;
        while (!allItems.equals(foundItems) && count < 1000)
        {
            final int nextValue = randomFrom(fourItemArray);
            assertTrue(allItems.contains(nextValue));
            foundItems.add(nextValue);
            count += 1;
        }
    }

    @Test(expected = RuntimeException.class)
    public void testRandomFromExceptionCaseWithNull()
    {
        int[] nullIntsArray = null;

        //exception cases
        randomFrom(nullIntsArray);
    }

    @Test(expected = RuntimeException.class)
    public void testRandomFromExceptionCaseWithEmptyArrayF()
    {
        //exception cases
        randomFrom(new int[]{});
    }

    @Test
    public void testRandomAlphanum()
    {
        final Random r = new Random(System.currentTimeMillis());
        final int length = r.nextInt(20) + 1;
        //chances of a randomizer producing the same exact values 4 consecutive times should be infinitisimal
        assertFalse(hashSet(Randoms.randomAlphanumeric(length), Randoms.randomAlphanumeric(length),
                            Randoms.randomAlphanumeric(length), Randoms.randomAlphanumeric(length)).size() == 1);
    }


    @Test
    public void testRandomFloat()
    {
        assertFalse(hashSet(Randoms.randomFloat(), Randoms.randomFloat(), Randoms.randomFloat(),
                            Randoms.randomFloat()).size() == 1);
    }

    @Test
    public void testRandomCombinationOf()
    {
        final Supplier<? extends Collection<Dummy>> factory = () -> new ArrayList<Dummy>();
        assertFalse(hashSet(randomFrom(Dummy.values()), randomFrom(factory, Dummy.values()),
                            randomFrom(factory, Dummy.values()), randomFrom(factory, Dummy.values())).size() == 1);
    }

    @Test
    public void testRandomStringWithTwoInts()
    {
        assertFalse(hashSet(Randoms.randomString(1, 10), Randoms.randomString(1, 10), Randoms.randomString(1, 10),
                            Randoms.randomString(1, 10)).size() == 1);

        assertFalse(hashSet(Randoms.randomString(1, 10)
                                   .length(), Randoms.randomString(1, 10)
                                                     .length(), Randoms.randomString(1, 10)
                                                                       .length(), Randoms.randomString(1, 10)
                                                                                         .length()).size() == 1);
    }

    @Test
    public void testRandomStringWithOneInt()
    {
        assertFalse(hashSet(Randoms.randomString(10), Randoms.randomString(10), Randoms.randomString(10),
                            Randoms.randomString(10)).size() == 1);
    }

    @Test
    public void testRandomFromWithCollections()
    {
        //given
        final Collection<String> coll = asList("a", "b", "c");

        //when
        final String result = randomFrom(coll);

        //then
        assertNotNull(result);
        assertTrue(coll.contains(result));
        while (true)
        {
            if (randomFrom(coll).equals(randomFrom(coll)))
            {
                break;
            }
        }
    }

    @Test
    public void random_string_factory_returns_different_reuslts() {
        final Supplier<String> stringSupplier = Randoms.randomStringFactory(5);
        while(stringSupplier.get().equals(stringSupplier.get()))
        {
            //no-op
        }
        assertThat(stringSupplier.get()).hasSize(5);
    }

    @Test
    public void random_string_with_many_params_will_return_different_results() {
        final int count = 100;
        final int start = 0;
        final int end = 10;
        final boolean letters = true;
        final boolean numbers = true;
        final char chars[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
        while(Randoms.randomString(count, start, end, letters, numbers, chars)
                     .equals(
                             Randoms.randomString(count, start, end, letters, numbers, chars)
                            )
                )
        {
            //no-op
        }
    }

    @Test
    public void random_string_with_many_params_will_return_different_results2() {
        final int count = 100;
        final int start = 0;
        final int end = 10;
        final boolean letters = false;
        final boolean numbers = false;
        final char chars[] = null;
        while(Randoms.randomString(count, start, end, letters, numbers, chars)
                     .equals(
                             Randoms.randomString(count, start, end, letters, numbers, chars)
                            )
                )
        {
            //no-op
        }
    }


    @Test
    public void random_string_will_not_allow_negative_count() {
        final int count = -1;
        final int start = 0;
        final int end = 10;
        final boolean letters = true;
        final boolean numbers = true;
        final char chars[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
        assertThat(catchThrowable(() ->
                Randoms.randomString(count, start, end, letters, numbers, chars)
                                 )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void random_string_will_not_allow_empty_char_array() {
        final int count = 1;
        final int start = 0;
        final int end = 10;
        final boolean letters = true;
        final boolean numbers = true;
        final char chars[] = {};
        assertThat(catchThrowable(() ->
                                          Randoms.randomString(count, start, end, letters, numbers, chars)
                                 )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void random_string_will_not_allow_end_less_than_start() {
        final int count = 1;
        final int start = 10;
        final int end = 1;
        final boolean letters = true;
        final boolean numbers = true;
        final char chars[] = {'a', 'b'};
        assertThat(catchThrowable(() ->
                                          Randoms.randomString(count, start, end, letters, numbers, chars)
                                 )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void random_string_will_assume_end_to_be_char_array_length_if_start_and_end_are_zero() {
        final int count = 1;
        final int start = 0;
        final int end = 0;
        final boolean letters = true;
        final boolean numbers = true;
        final char chars[] = {'a', 'b', 'c'};
        assertThat(Randoms.randomString(count, start, end, letters, numbers, chars)).hasSize(1);
    }

    @Test
    public void random_numeric_factory_returns_different_results() {
        final Supplier<String> stringSupplier = Randoms.randomNumericFactory(5);
        while(stringSupplier.get().equals(stringSupplier.get()))
        {
            //no-op
        }
        assertThat(stringSupplier.get())
                .hasSize(5)
                .containsOnlyDigits();
    }

    @Test
    public void random_alphabetic_factory_returns_different_results() {
        final Supplier<String> stringSupplier = Randoms.randomAlphabeticFactory(5);
        while(stringSupplier.get().equals(stringSupplier.get()))
        {
            //no-op
        }
        assertThat(stringSupplier.get())
                .hasSize(5)
                .containsPattern("[a-zA-Z]{5}");
    }

    @Test
    public void random_alphanumeric_factory_returns_different_results() {
        final Supplier<String> stringSupplier = Randoms.randomAlphanumericFactory(5);
        while(stringSupplier.get().equals(stringSupplier.get()))
        {
            //no-op
        }
        assertThat(stringSupplier.get())
                .hasSize(5)
                .containsPattern("[a-zA-Z0-9]{5}");
    }

    @Test
    public void random_ascii_factory_returns_different_results() {
        final Supplier<String> stringSupplier = Randoms.randomAsciiFactory(5);
        while(stringSupplier.get().equals(stringSupplier.get()))
        {
            //no-op
        }
        assertThat(stringSupplier.get())
                .hasSize(5)
                .containsPattern("[\\x20-\\x7F]{5}");
    }

    @Test
    public void random_set_from_should_return_different_results() {
        while(Randoms.randomSetFrom(asList(1, 1, 2, 4, 5)).equals(
                Randoms.randomSetFrom(asList(1, 1, 2, 4, 5))
                                                                 ))
        {
            //no-op
        }
    }

    @Test
    public void random_set_from_should_return_different_results_same_size() {
        while(Randoms.randomSetFrom(3, asList(1, 1, 2, 4, 5)).equals(
                Randoms.randomSetFrom(3, asList(1, 1, 2, 4, 5))
                                                                 ))
        {
            //no-op
        }
        assertThat(Randoms.randomSetFrom(3, asList(1, 1, 2, 4, 5))).hasSize(3);
    }

    @Test
    public void testRandomFromWithArrays()
    {
        //given
        final String[] array = {"a", "b", "c"};

        //when
        final String result = randomFrom(array);

        //then
        assertNotNull(result);
        assertTrue(asList(array).contains(result));
        while (true)
        {
            if (!randomFrom(array).equals(randomFrom(array)))
            {
                break;
            }
        }
    }

    @Test
    public void testRandomDateBetween()
    {
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
    public void testRandomDateBetween_DateTime()
    {
        //given
        final LocalDateTime first = LocalDateTime.now();
        final Date firstInstant = Date.from(first.atZone(ZoneId.systemDefault())
                                                 .toInstant());

        //50 days ahead
        final LocalDateTime second = first.plusDays(50);
        final Date secondInstant = Date.from(second.atZone(ZoneId.systemDefault())
                                                   .toInstant());

        //when
        final LocalDateTime randomDate = Randoms.randomDateBetween(first, second);
        final Date randomDateInstant = Date.from(randomDate.atZone(ZoneId.systemDefault())
                                                           .toInstant());

        //then
        assertTrue(String.format("dateTime[%s] is not between lower[%s] and upper[%s]", randomDate, first, second),
                   randomDateInstant.getTime() >= firstInstant.getTime() && randomDateInstant.getTime() <= secondInstant.getTime());
    }

    @Test
    public void testRandomNotInForStringsGoodFactory()
    {
        //given
        final Supplier<String> stringFactory = new Supplier<String>()
        {
            private final String[] values = {"1", "2", "3", "4", "5"};
            private final CyclicIndex cyclicIndex = new CyclicIndex(values.length);

            @Override
            public String get()
            {
                return values[cyclicIndex.next()];
            }
        };

        final Collection<String> unwantedValues = asList("1", "2", "3", "4");

        //when
        final String wantedValue = Randoms.randomNotIn(stringFactory, unwantedValues);

        //then
        assertEquals("5", wantedValue);
    }

    @Test
    public void testRandomNotInForIntegersGoodFactory()
    {
        //given
        final Supplier<Integer> stringFactory = new Supplier<Integer>()
        {
            private final Integer[] values = {1, 2, 3, 4, 5};
            private final CyclicIndex cyclicIndex = new CyclicIndex(values.length);

            @Override
            public Integer get()
            {
                return values[cyclicIndex.next()];
            }
        };

        final Collection<Integer> unwantedValues = asList(1, 2, 3, 4);

        //when
        final int wantedValue = Randoms.randomNotIn(stringFactory, unwantedValues);

        //then
        assertEquals(5, wantedValue);
    }

    @Test
    public void testRandomNotInForIntegersGoodFactoryFirstValueReturned()
    {
        //given
        final Supplier<Integer> stringFactory = new Supplier<Integer>()
        {
            private final Integer[] values = {1, 2, 3, 4, 5};
            private final CyclicIndex cyclicIndex = new CyclicIndex(values.length);

            @Override
            public Integer get()
            {
                return values[cyclicIndex.next()];
            }
        };

        final Collection<Integer> unwantedValues = asList(2, 3, 4, 5);

        //when
        final int wantedValue = Randoms.randomNotIn(stringFactory, unwantedValues);

        //then
        assertEquals(1, wantedValue);
    }

    @Test
    public void testRandomDateFactory()
    {
        IntStream.range(0, 100)
                 .forEach(i -> assertNotNull(Randoms.RANDOM_DATETIME_FACTORY.get()));
    }

    @Test
    public void testRandomCaseDoesRandomizeCases()
    {
        //given
        final TestCasingEnum enumMember = randomFrom(TestCasingEnum.class);

        //when
        final Set<String> randomCaseSet = Sets.newSet(Randoms.randomCase(enumMember.name()),
                                                      Randoms.randomCase(enumMember.name()),
                                                      Randoms.randomCase(enumMember.name()),
                                                      Randoms.randomCase(enumMember.name()),
                                                      Randoms.randomCase(enumMember.name()),
                                                      Randoms.randomCase(enumMember.name()),
                                                      Randoms.randomCase(enumMember.name()));
        final Set<String> toLowerCase = toLowerCase(randomCaseSet);

        //then
        assertTrue(randomCaseSet.toString(), randomCaseSet.size() > 1);
        assertTrue(randomCaseSet.toString(), toLowerCase.size() == 1);
        assertTrue(randomCaseSet.toString(), toLowerCase.contains(enumMember.name()
                                                                            .toLowerCase()));
    }

    @Test
    public void testRandomXml10String()
    {
        assertEquals("", Randoms.randomXml10String(0));

        assertFalse(Randoms.randomXml10String(10000)
                           .matches(Randoms.XML_1_0PATTERN));
        assertFalse(Randoms.randomXml10String(0, 10000)
                           .matches(Randoms.XML_1_0PATTERN));
    }

    @Test
    public void random_enum_factory_returns_different_members()
    {
        final Supplier<TestEnum> testEnumSupplier = Randoms.randomEnumFactory(TestEnum.class);
        while(testEnumSupplier.get() == testEnumSupplier.get())
        {
            //no-op
        }
    }

    //utils

    private Set<String> toLowerCase(final Set<String> set)
    {
        return set.stream()
                  .map(String::toLowerCase)
                  .collect(toSet());
    }

    //test utils

    private enum Dummy
    {

        A,
        B,
        C
    }

    //test
    private enum TestEnum
    {
        A,
        B,
        C;
    }

    private enum TestCasingEnum
    {
        JESUS,
        MOSES,
        BUDDHA;
    }
}
