package com.codebreeze.testing;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static com.codebreeze.testing.PintoCollections.hashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class PintoCollectionsTest
{
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

}
