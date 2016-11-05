package com.codebreeze.testing;

import org.junit.Test;

import java.util.*;
import java.util.stream.IntStream;

import static com.codebreeze.testing.PintoCollections.hashSet;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class PintoCollectionsTest
{
    @Test
    public void cannot_be_instantiated()
    {
        Verify.uninstantiable(PintoCollections.class);
    }

    @Test
    public void isEmpty_should_cope_with_null_and_empty_and_full_collections_correctly()
    {
        assertThat(PintoCollections.isEmpty(null)).isTrue();
        assertThat(PintoCollections.isEmpty(hashSet())).isTrue();
        assertThat(PintoCollections.isEmpty(hashSet(1))).isFalse();
    }

    @Test
    public void hashSet_should_cope_with_null()
    {
        //given
        final Integer[] ints = null;

        //when
        final Throwable thrown = catchThrowable(() -> PintoCollections.hashSet(ints));
        //then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void hashSet_should_cope_with_empty()
    {
        //given
        final Integer[] ints = {};

        //when
        final Set<Integer> set = PintoCollections.hashSet(ints);
        //then
        assertThat(set).isEmpty();
    }

    @Test
    public void hashSet_should_cope_with_normal_elements()
    {
        //given
        final Integer[] ints = {1, 2, 3};

        //when
        final Set<Integer> set = PintoCollections.hashSet(ints);
        //then
        assertThat(set).containsOnly(1, 2, 3);
    }

    @Test
    public void hashSet_should_cope_with_duplicate_elements()
    {
        //given
        final Integer[] ints = {1, 2, 2};

        //when
        final Set<Integer> set = PintoCollections.hashSet(ints);

        //then
        assertThat(set).containsOnly(1, 2);
    }

    @Test
    public void powerSet_produces_correct_power_sets()
    {
        //given
        final Set<Integer> ints = new HashSet<Integer>()
        {{
            add(1);
            add(2);
            add(3);
        }};

        //when
        final Set<Set<Integer>> sets = PintoCollections.powerSet(ints);

        //then
        assertThat(sets).containsOnly(hashSet(), hashSet(1), hashSet(2), hashSet(3), hashSet(1, 2), hashSet(1, 3),
                                      hashSet(2, 3), hashSet(1, 2, 3));
    }

    @Test(expected = java.lang.UnsupportedOperationException.class)
    public void powerSet_iterator_does_not_allow_removal()
    {
        //given
        final Set<Integer> ints = new HashSet<Integer>()
        {{
            add(1);
            add(2);
            add(3);
        }};

        //when
        final Set<Set<Integer>> sets = PintoCollections.powerSet(ints);

        //then
        sets.iterator().remove();
        fail("should have thrown exception as powerset iterators do not allow removals");
    }

    @Test
    public void powerSet_contains_works_with_indices()
    {
        //given
        final Set<Integer> ints = new HashSet<Integer>()
        {{
            add(1);
            add(2);
            add(3);
        }};

        //when
        final Set<Set<Integer>> sets = PintoCollections.powerSet(ints);

        //then
        assertThat(sets.contains(new ArrayList<>())).isFalse();
        assertThat(sets.contains(new HashSet<>())).isTrue();
        assertThat(sets.contains(new HashSet<>(asList(1)))).isTrue();
        assertThat(sets.contains(new HashSet<>(asList(1, 2)))).isTrue();
        assertThat(sets.contains(new HashSet<>(asList(1, 2, 3)))).isTrue();
        assertThat(sets.contains(new HashSet<>(asList(1, 2, 3, 4)))).isFalse();
        assertThat(sets.iterator().next().contains(4)).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void powerSet_iterator_throws_no_such_element_exception_once_no_more_elements()
    {
        //given
        final Set<Integer> ints = new HashSet<Integer>()
        {{
            add(1);
            add(2);
        }};

        //when
        final Iterator<Set<Integer>> setsIterator = PintoCollections.powerSet(ints).iterator();

        //then
        while(true)
        {
            setsIterator.next();
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void powerSet_iterator_throws_no_such_element_exception_once_no_more_elements2()
    {
        //given
        final Set<Integer> ints = new HashSet<Integer>()
        {{
            IntStream.range(0, 30).forEach(i -> add(i));
        }};

        //when
        final Iterator<Set<Integer>> setsIterator = PintoCollections.powerSet(ints).iterator();

        //then
        while (setsIterator.hasNext())
        {
            final Set<Integer> next = setsIterator.next();
            final Iterator<Integer> iterator = next.iterator();
            while (true)
            {
                iterator.next();
            }
        }
    }

    @Test
    public void powerSet_equals_and_hash_code_correctly_identifies_same_power_sets()
    {
        //given
        final Set<Integer> ints1 = new HashSet<Integer>()
        {{
            add(1);
            add(2);
            add(3);
        }};

        final Set<Integer> ints2 = new HashSet<Integer>()
        {{
            add(3);
            add(2);
            add(1);
        }};

        //when
        final Set<Set<Integer>> sets1 = PintoCollections.powerSet(ints1);
        final Set<Set<Integer>> sets2 = PintoCollections.powerSet(ints2);

        final Iterator<Set<Integer>> iterator = sets1.iterator();
        while(iterator.hasNext())
        {
            iterator.next();
        }

        //then
        assertThat(sets1).isEqualTo(sets2);
        assertThat(sets1.equals(sets2)).isTrue();
        assertThat(sets1.hashCode()).isEqualTo(sets2.hashCode());
    }

    @Test
    public void powerSet_is_never_empty()
    {
        //given
        final Set<Integer> ints = new HashSet<>();

        //when
        final Set<Set<Integer>> sets = PintoCollections.powerSet(ints);

        //then
        assertFalse(sets.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void powerSet_rejects_power_sets_of_more_than_30()
    {
        //given
        final Set<Integer> ints = IntStream.range(0,31)
                                           .mapToObj(i -> i)
                                           .collect(toSet());

        //when
        PintoCollections.powerSet(ints);

        //then
        fail("should never have gotten that far");
    }

    @Test
    public void powerSet_should_return_empty_set_in_power_set_for_empty_input()
    {
        //given
        final Set<Integer> ints = new HashSet<>();

        //when
        final Set<Set<Integer>> sets = PintoCollections.powerSet(ints);

        //then
        assertThat(sets).contains(hashSet());
    }

    @Test
    public void powerSet_should_not_accept_null_input()
    {
        //given
        final Set<Integer> ints = null;

        //when
        final Throwable thrown = catchThrowable(() -> PintoCollections.powerSet(ints));

        //then
        assertThat(thrown).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void takeWhile_will_do_good()
    {
        // take a while
        assertThat(PintoCollections.takeWhile(0, i -> ++i, i -> i < 10).toArray())
                .containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }
}
